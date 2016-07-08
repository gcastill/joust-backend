package com.joust.backend.web.mvc.controller.oauth.google;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.net.URL;
import java.util.Arrays;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.joust.backend.model.domain.User;
import com.joust.backend.model.domain.User.UserSource;

@Controller
@RequestMapping("/oath/google")
public class GoogleController {

  @Resource
  private String googleClientId;

  @Resource
  private JsonFactory googleJsonFactory;

  @Resource
  private HttpTransport googleHttpTransport;

  @Resource
  private String googleIssuer;

  @RequestMapping(method = POST)
  public ResponseEntity<User> verifyGoogleLogin(@RequestBody String idToken) throws Exception {
    // TODO: clean this mess up. This should be in a service. There
    // shouldn't be any business logic inside a controller.
    // Also, System.out < proper logging.
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(googleHttpTransport, googleJsonFactory)
        .setAudience(Arrays.asList(googleClientId)).setIssuer(googleIssuer).build();

    GoogleIdToken googleIdToken = verifier.verify(idToken);
    if (googleIdToken != null) {
      Payload payload = googleIdToken.getPayload();

      User result = new User();

      result.setSource(UserSource.GOOGLE);
      result.setReferenceId(payload.getSubject());

      if (!payload.getEmailVerified()) {
        // TODO: throw an exception? that seems like the cheapest way to handle
        // this case. We don't want fake google users running around in our
        // system.
      }

      result.setEmail(payload.getEmail());

      String pictureUrl = (String) payload.get("picture");
      result.setProfileUrl(pictureUrl != null ? new URL(pictureUrl) : null);

      String locale = (String) payload.get("locale");
      result.setLocale(locale != null ? Locale.forLanguageTag(locale) : null);

      result.setFamilyName((String) payload.get("family_name"));
      result.setGivenName((String) payload.get("given_name"));

      // TODO: this is a good place to perform some sort of merge logic. do we
      // refresh the user's data from google each time? should we never update
      // this information and allow the user to update it themselves in our own
      // UI? Questions, so many questions...

      return new ResponseEntity<>(result, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

  }
}
