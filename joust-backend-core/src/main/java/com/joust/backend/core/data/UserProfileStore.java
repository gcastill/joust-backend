package com.joust.backend.core.data;

import java.util.UUID;

import com.joust.backend.core.model.ExternalProfileSource;
import com.joust.backend.core.model.ExternalProfileSource.Source;
import com.joust.backend.core.model.UserProfile;

public interface UserProfileStore {

	UserProfile getUserProfile(UUID id);

	UserProfile getUserProfileByExternalSource(Source source, String referenceId);

	void mergeUserProfile(UserProfile profile);

	void saveExternalProfileSource(ExternalProfileSource externalProfileSource);
}
