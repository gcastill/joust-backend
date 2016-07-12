package com.joust.backend.web.mvc.controller.oauth.google;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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
import com.joust.backend.core.model.UserProfile.UserProfileBuilder;

import lombok.Data;

@Controller
@RequestMapping("/oauth/google")
@Data
public class GoogleController {

  private static Set<GrantedAuthority> DEFAULT_USER_AUTHORITIES = Collections
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
  private UserDetailsManager userDetailsService;

  @Resource
  private UserProfileStore userProfileStore;

  @Resource
  private TokenEndpoint tokenEndpoint;

  private UserProfile verifyGoogleLogin(String idToken) throws IOException, GeneralSecurityException {
    // TODO: clean this mess up. This should be in a service. There
    // shouldn't be any business logic inside a controller.

    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(googleHttpTransport, googleJsonFactory)
        .setAudience(Arrays.asList(googleClientId)).setIssuer(googleIssuer).build();

    GoogleIdToken googleIdToken = verifier.verify(idToken);
    if (googleIdToken == null) {
      throw new GeneralSecurityException("googleIdToken is null");
    }
    Payload payload = googleIdToken.getPayload();

    Source source = Source.GOOGLE;
    String referenceId = payload.getSubject();

    UserProfile fromDatabase = userProfileStore.getUserProfileByExternalSource(source, referenceId);
    boolean newUser = fromDatabase == null;

    UserProfileBuilder builder = newUser ? UserProfile.builder().id(UUID.randomUUID()) : fromDatabase.toBuilder();

    if (!payload.getEmailVerified()) {
      // TODO: throw an exception? that seems like the cheapest way to
      // handle
      // this case. We don't want fake google users running around in
      // our
      // system.
    }

    builder.email(payload.getEmail());

    String pictureUrl = (String) payload.get("picture");
    builder.profileUrl(pictureUrl != null ? new URL(pictureUrl) : null);

    String locale = (String) payload.get("locale");
    builder.locale(locale != null ? Locale.forLanguageTag(locale) : null);

    builder.familyName((String) payload.get("family_name"));
    builder.givenName((String) payload.get("given_name"));

    // We merge user data from google each time.
    UserProfile updated = builder.build();
    userProfileStore.mergeUserProfile(updated);

    if (newUser) {
      userProfileStore.saveExternalProfileSource(ExternalProfileSource.builder().source(source).referenceId(referenceId)
          .userProfileId(updated.getId()).build());
    }

    return updated;

  }

  @RequestMapping(method = POST, headers = "x-google-token")
  public ResponseEntity<Map<String, Object>> token(Authentication authenication,
      @RequestHeader("x-google-token") String googleToken, @RequestParam Map<String, String> parameters)
      throws IOException, GeneralSecurityException, HttpRequestMethodNotSupportedException {

    UserProfile joustUser = verifyGoogleLogin(googleToken);
    UserDetails userPrincipal = null;
    String username = joustUser.getId().toString();

    if (!userDetailsService.userExists(username)) {
      userPrincipal = new User(username, generatePlaceholderPassword(), true, true, true, true,
          DEFAULT_USER_AUTHORITIES);
      userDetailsService.createUser(userPrincipal);

    } else {
      userPrincipal = userDetailsService.loadUserByUsername(username);

    }

    parameters.put("username", userPrincipal.getUsername());
    parameters.put("password", userPrincipal.getPassword());

    return getResponse(joustUser, tokenEndpoint.postAccessToken(authenication, parameters).getBody());
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

}
