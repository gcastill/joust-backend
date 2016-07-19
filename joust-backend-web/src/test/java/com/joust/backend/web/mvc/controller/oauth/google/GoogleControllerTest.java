package com.joust.backend.web.mvc.controller.oauth.google;

import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.provisioning.UserDetailsManager;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.joust.backend.core.data.UserProfileStore;
import com.joust.backend.test.meanbean.BeanTesterBuilder;

@RunWith(MockitoJUnitRunner.class)
public class GoogleControllerTest {
  @Mock
  private TokenEndpoint mockTokenEndPoint;

  @Mock
  private JsonFactory mockGoogleJsonFactory;

  @Mock
  private HttpTransport mockGoogleHttpTransport;

  @Mock
  private UserDetailsManager mockUserDetailsService;

  @Mock
  private UserProfileStore mockUserProfileStore;

  @Test
  public void testBean() {
    new BeanTesterBuilder().withFactory(TokenEndpoint.class, () -> {
      return mockTokenEndPoint;
    }).withFactory(JsonFactory.class, () -> {
      return mockGoogleJsonFactory;
    }).withFactory(HttpTransport.class, () -> {
      return mockGoogleHttpTransport;
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
