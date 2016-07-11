package com.joust.backend.core.model;

import java.util.UUID;

public class ExternalProfileSource {

	public static enum Source {
		GOOGLE, FACEBOOK;

	}

	private Source source;
	private String referenceId;
	private UUID userProfileId;

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public UUID getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(UUID userProfileId) {
		this.userProfileId = userProfileId;
	}

}
