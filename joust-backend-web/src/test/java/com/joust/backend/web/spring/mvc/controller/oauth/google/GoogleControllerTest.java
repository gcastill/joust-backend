package com.joust.backend.web.spring.mvc.controller.oauth.google;

import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.provisioning.UserDetailsManager;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.joust.backend.core.data.UserProfileStore;
import com.joust.backend.test.meanbean.BeanTesterBuilder;

@RunWith(MockitoJUnitRunner.class)
public class GoogleControllerTest {
  @Mock
  private TokenEndpoint mockTokenEndPoint;

  @Mock
  private GoogleIdTokenVerifier mockGoogleIdTokenVerifier;
  @Mock
  private UserDetailsManager mockUserDetailsService;

  @Mock
  private UserProfileStore mockUserProfileStore;

  @Test
  public void testBean() {
    new BeanTesterBuilder().withFactory(TokenEndpoint.class, () -> {
      return mockTokenEndPoint;
    }).withFactory(GoogleIdTokenVerifier.class, () -> {
      return mockGoogleIdTokenVerifier;
    }).withFactory(UserDetailsManager.class, () -> {
      return mockUserDetailsService;
    }).withFactory(UserProfileStore.class, () -> {
      return mockUserProfileStore;
    }).build().testBean(GoogleController.class);

  }

  @Test
  public void testGeneratePlaceholderPassword() throws Exception {
    assertNotNull(UUID.fromString(GoogleController.generatePlaceholderPassword()));
  }
}
