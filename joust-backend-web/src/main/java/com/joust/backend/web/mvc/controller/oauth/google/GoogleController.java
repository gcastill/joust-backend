package com.joust.backend.web.mvc.controller.oauth.google;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.joust.backend.core.data.UserProfileStore;
import com.joust.backend.core.model.ExternalProfileSource;
import com.joust.backend.core.model.ExternalProfileSource.Source;
import com.joust.backend.core.model.UserProfile;

@Controller
@RequestMapping("/oauth/google")
public class GoogleController {

	private Set<GrantedAuthority> DEFAULT_USER_AUTHORITIES = Collections
			.singleton(new SimpleGrantedAuthority("ROLE_USER"));

	@Resource
	private String googleClientId;

	@Resource
	private JsonFactory googleJsonFactory;

	@Resource
	private HttpTransport googleHttpTransport;

	@Resource
	private String googleIssuer;

	@Resource
	private DefaultTokenServices tokenServices;

	@Resource
	private UserDetailsManager userDetailsService;

	@Resource
	private UserProfileStore userProfileStore;

	@Resource
	private ClientDetailsService clientDetailsService;

	@Resource
	private OAuth2RequestFactory oauth2RequestFactory;

	private UserProfile verifyGoogleLogin(String idToken) throws IOException, GeneralSecurityException {
		// TODO: clean this mess up. This should be in a service. There
		// shouldn't be any business logic inside a controller.
		// Also, System.out < proper logging.
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(googleHttpTransport, googleJsonFactory)
				.setAudience(Arrays.asList(googleClientId)).setIssuer(googleIssuer).build();

		GoogleIdToken googleIdToken = verifier.verify(idToken);
		if (googleIdToken == null) {
			throw new GeneralSecurityException("googleIdToken is null");
		}
		Payload payload = googleIdToken.getPayload();

		ExternalProfileSource profileSource = new ExternalProfileSource();

		profileSource.setSource(Source.GOOGLE);
		profileSource.setReferenceId(payload.getSubject());

		UserProfile result = userProfileStore.getUserProfileByExternalSource(profileSource.getSource(),
				profileSource.getReferenceId());

		boolean newUser = false;
		if (result == null) {
			result = new UserProfile();
			result.setId(UUID.randomUUID());

			profileSource.setUserProfileId(result.getId());
			newUser = true;

		}

		if (!payload.getEmailVerified()) {
			// TODO: throw an exception? that seems like the cheapest way to
			// handle
			// this case. We don't want fake google users running around in
			// our
			// system.
		}

		result.setEmail(payload.getEmail());

		String pictureUrl = (String) payload.get("picture");
		result.setProfileUrl(pictureUrl != null ? new URL(pictureUrl) : null);

		String locale = (String) payload.get("locale");
		result.setLocale(locale != null ? Locale.forLanguageTag(locale) : null);

		result.setFamilyName((String) payload.get("family_name"));
		result.setGivenName((String) payload.get("given_name"));

		// We merge user data from google each time.
		userProfileStore.mergeUserProfile(result);

		if (newUser) {
			userProfileStore.saveExternalProfileSource(profileSource);
		}

		return result;

	}

	@RequestMapping(method = POST, headers = "x-google-token")
	public ResponseEntity<Map<String, Object>> token(Principal principal,
			@RequestHeader("x-google-token") String googleToken, @RequestParam Map<String, String> parameters)
			throws IOException, GeneralSecurityException {

		if (!(principal instanceof Authentication)) {
			throw new InsufficientAuthenticationException(
					"There is no client authentication. Try adding an appropriate authentication filter.");
		}

		String clientId = getClientId(principal);
		ClientDetails authenticatedClient = clientDetailsService.loadClientByClientId(clientId);

		TokenRequest tokenRequest = oauth2RequestFactory.createTokenRequest(parameters, authenticatedClient);

		UserProfile joustUser = verifyGoogleLogin(googleToken);
		String username = joustUser.getId().toString();

		Collection<GrantedAuthority> authorities = authenticatedClient.getAuthorities();

		OAuth2Request oAuth2Request = oauth2RequestFactory.createOAuth2Request(authenticatedClient, tokenRequest);

		UserDetails userPrincipal = null;

		if (!userDetailsService.userExists(username)) {
			userPrincipal = new User(username, generatePlaceholderPassword(), true, true, true, true,
					DEFAULT_USER_AUTHORITIES);
			userDetailsService.createUser(userPrincipal);

		} else {
			userPrincipal = userDetailsService.loadUserByUsername(username);

		}

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal,
				null, authorities);
		OAuth2Authentication auth = new OAuth2Authentication(oAuth2Request, authenticationToken);

		OAuth2AccessToken token = tokenServices.createAccessToken(auth);
		return getResponse(joustUser, token);
	}

	private ResponseEntity<Map<String, Object>> getResponse(UserProfile profile, OAuth2AccessToken accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");

		Map<String, Object> result = new HashMap<>();
		result.put("profile", profile);
		result.put("token", accessToken);
		return new ResponseEntity<>(result, headers, HttpStatus.OK);
	}

	public static String generatePlaceholderPassword() {
		return UUID.randomUUID().toString();
	}

	/**
	 * @param principal
	 *            the currently authentication principal
	 * @return a client id if there is one in the principal
	 */
	public static String getClientId(Principal principal) {
		Authentication client = (Authentication) principal;
		if (!client.isAuthenticated()) {
			throw new InsufficientAuthenticationException("The client is not authenticated.");
		}
		String clientId = client.getName();
		if (client instanceof OAuth2Authentication) {
			// Might be a client and user combined authentication
			clientId = ((OAuth2Authentication) client).getOAuth2Request().getClientId();
		}
		return clientId;
	}

}
