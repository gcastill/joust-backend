package com.joust.backend.core.model;

import java.util.UUID;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
public class ExternalProfileSource {

	public static enum Source {
		GOOGLE, FACEBOOK;

	}

	private Source source;
	private String referenceId;
	private UUID userProfileId;

}
