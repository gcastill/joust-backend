package com.joust.be.web.controller.google;

import java.util.Arrays;

import javax.annotation.Resource;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.stereotype.Controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

@Controller
@Path("/google")
public class GoogleController {

	@Resource
	private String googleClientId;

	@Resource
	private JsonFactory googleJsonFactory;

	@Resource
	private HttpTransport googleHttpTransport;

	@Resource
	private String googleIssuer;

	@Path("/verifyLogin")
	@POST
	public Response verifyGoogleLogin(String idToken) throws Exception {
		// TODO: clean this mess up. This should be in a service. There
		// shouldn't be any business logic inside a controller.
		// Also, System.out < proper logging.
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(googleHttpTransport, googleJsonFactory)
				.setAudience(Arrays.asList(googleClientId)).setIssuer(googleIssuer).build();

		GoogleIdToken googleIdToken = verifier.verify(idToken);
		if (googleIdToken != null) {
			Payload payload = googleIdToken.getPayload();

			// Print user identifier
			String userId = payload.getSubject();
			System.out.println("User ID: " + userId);

			System.out.println(payload);
			// Get profile information from payload
			String email = payload.getEmail();
			boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
			String name = (String) payload.get("name");
			String pictureUrl = (String) payload.get("picture");
			String locale = (String) payload.get("locale");
			String familyName = (String) payload.get("family_name");
			String givenName = (String) payload.get("given_name");

			// Use or store profile information
			// ...
			return Response.ok().build();
		} else {
			return Response.status(Status.BAD_REQUEST).build();
		}

	}
}
