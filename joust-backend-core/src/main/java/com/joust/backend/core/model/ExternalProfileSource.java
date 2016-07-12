package com.joust.backend.core.model;

import java.net.URL;
import java.util.Locale;
import java.util.UUID;

import com.joust.backend.core.model.UserProfile.UserProfileBuilder;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class ExternalProfileSource {

	public static enum Source {
		GOOGLE, FACEBOOK;

	}

	private Source source;
	private String referenceId;
	private UUID userProfileId;

}
