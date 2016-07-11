package com.joust.backend.core.data;

import com.joust.backend.core.model.ExternalProfileSource;
import com.joust.backend.core.model.ExternalProfileSource.Source;
import com.joust.backend.core.model.UserProfile;

public interface UserProfileStore {

	UserProfile getUserProfile(String id);

	UserProfile getUserProfileByExternalSource(Source source, String referenceId);

	void mergeUserProfile(UserProfile profile);

	void saveExternalProfileSource(ExternalProfileSource externalProfileSource);
}
