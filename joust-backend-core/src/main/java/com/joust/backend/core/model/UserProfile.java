package com.joust.backend.core.model;

import java.net.URL;
import java.util.Locale;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class UserProfile {

	private UUID id;
	private String email;
	private String givenName;
	private String familyName;
	private Locale locale;
	private URL profileUrl;

}
