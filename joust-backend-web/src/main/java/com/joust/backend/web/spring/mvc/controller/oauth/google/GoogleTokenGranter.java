package com.joust.backend.web.spring.mvc.controller.oauth.google;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.joust.backend.core.data.UserProfileStore;
import com.joust.backend.core.model.ExternalProfileSource;
import com.joust.backend.core.model.ExternalProfileSource.Source;
import com.joust.backend.core.model.UserProfile;
import com.joust.backend.core.model.UserProfile.UserProfileBuilder;

public class GoogleTokenGranter extends AbstractTokenGranter {

  private static final String GRANT_TYPE = "google_token";

  private static Set<GrantedAuthority> DEFAULT_USER_AUTHORITIES = Collections
      .singleton(new SimpleGrantedAuthority("ROLE_USER"));

  private UserDetailsManager userDetailsService;
  private UserProfileStore userProfileStore;
  private GoogleIdTokenVerifier verifier;

  public GoogleTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
      OAuth2RequestFactory requestFactory, UserDetailsManager userDetailsService, UserProfileStore userProfileStore,
      GoogleIdTokenVerifier verifier) {
    super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    this.userDetailsService = userDetailsService;
    this.userProfileStore = userProfileStore;
    this.verifier = verifier;
  }

  @Override
  @Transactional
  protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

    Map<String, String> parameters = new LinkedHashMap<String, String>(tokenRequest.getRequestParameters());
    String googleToken = parameters.get("google_token");
    UserProfile userProfile = null;
    try {
      userProfile = verifyGoogleLogin(googleToken);
    } catch (IOException | GeneralSecurityException e) {
      throw new InvalidGrantException(e.getMessage());
    }
    String username = userProfile.getId().toString();
    UserDetails userPrincipal = null;
    if (!userDetailsService.userExists(username)) {
      userPrincipal = new User(username, generatePlaceholderPassword(), true, true, true, true,
          DEFAULT_USER_AUTHORITIES);
      userDetailsService.createUser(userPrincipal);

    } else {
      userPrincipal = userDetailsService.loadUserByUsername(username);
      // TODO: check if account is expired, locked, etc...
    }

    OAuth2Request request = getRequestFactory().createOAuth2Request(client, tokenRequest);

    return new OAuth2Authentication(request,
        new UsernamePasswordAuthenticationToken(username, null, userPrincipal.getAuthorities()));
  }

  private UserProfile verifyGoogleLogin(String idToken) throws IOException, GeneralSecurityException {
    // TODO: clean this mess up. This should be in a service. There
    // shouldn't be any business logic inside a controller.

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
      throw new GeneralSecurityException("Email not validated.");
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

  public static String generatePlaceholderPassword() {
    return UUID.randomUUID().toString();
  }
}
