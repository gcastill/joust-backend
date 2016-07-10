package com.joust.backend.web.mvc.controller.oauth.google;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.Arrays;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
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
import com.joust.backend.model.domain.JoustUser;
import com.joust.backend.model.domain.JoustUser.UserSource;

@Controller
@RequestMapping("/oauth/google")
public class GoogleController {

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
	UserDetailsManager userDetailsService;

	private JoustUser verifyGoogleLogin(String idToken) throws IOException, GeneralSecurityException {
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

		JoustUser result = new JoustUser();

		result.setSource(UserSource.GOOGLE);
		result.setReferenceId(payload.getSubject());
		result.setId(UUID.randomUUID());

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

		// TODO: this is a good place to perform some sort of merge logic.
		// do we
		// refresh the user's data from google each time? should we never
		// update
		// this information and allow the user to update it themselves in
		// our own
		// UI? Questions, so many questions...

		return result;

	}

	@RequestMapping(method = POST, headers = "x-google-token")
	public ResponseEntity<OAuth2AccessToken> token(Principal principal, @RequestHeader("x-google-token") String googleToken,
			@RequestParam Map<String, String> parameters) throws IOException, GeneralSecurityException {

		JoustUser joustUser = verifyGoogleLogin(googleToken);

		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		Map<String, String> requestParameters = new HashMap<>();
		String clientId = principal.getName();

		boolean approved = true;
		Set<String> scope = new HashSet<>();
		scope.add("scope");
		Set<String> resourceIds = new HashSet<>();
		Set<String> responseTypes = new HashSet<>();
		responseTypes.add("code");
		Map<String, Serializable> extensionProperties = new HashMap<>();

		OAuth2Request oAuth2Request = new OAuth2Request(requestParameters, clientId, authorities, approved, scope,
				resourceIds, null, responseTypes, extensionProperties);

		User userPrincipal = new User(joustUser.getId().toString(), "", true, true, true, true, authorities);

		userDetailsService.createUser(userPrincipal);

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal,
				null, authorities);
		OAuth2Authentication auth = new OAuth2Authentication(oAuth2Request, authenticationToken);
		OAuth2AccessToken token = tokenServices.createAccessToken(auth);
		return getResponse(token);
	}

	private ResponseEntity<OAuth2AccessToken> getResponse(OAuth2AccessToken accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");
		return new ResponseEntity<OAuth2AccessToken>(accessToken, headers, HttpStatus.OK);
	}

}
