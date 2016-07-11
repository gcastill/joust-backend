package com.joust.backend.data.spring;

import static com.joust.backend.data.spring.UserProfileColumns.EMAIL;
import static com.joust.backend.data.spring.UserProfileColumns.FAMILY_NAME;
import static com.joust.backend.data.spring.UserProfileColumns.GIVEN_NAME;
import static com.joust.backend.data.spring.UserProfileColumns.LOCALE;
import static com.joust.backend.data.spring.UserProfileColumns.PROFILE_URL;
import static com.joust.backend.data.spring.UserProfileColumns.USER_PROFILE_ID;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;

import com.joust.backend.core.model.UserProfile;

public class UserProfileRowMapper implements RowMapper<UserProfile> {

	@Override
	public UserProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserProfile result = new UserProfile();
		result.setId(UUID.fromString(rs.getString(USER_PROFILE_ID)));
		result.setEmail(rs.getString(EMAIL));
		result.setGivenName(rs.getString(GIVEN_NAME));
		result.setFamilyName(rs.getString(FAMILY_NAME));
		String profileUrlString = rs.getString(PROFILE_URL);
		try {
			result.setProfileUrl(profileUrlString == null ? new URL(profileUrlString) : null);
		} catch (MalformedURLException e) {
			throw new SQLException(e.getMessage(), e);
		}
		String localeString = rs.getString(LOCALE);
		result.setLocale(localeString == null ? Locale.forLanguageTag(localeString) : null);

		return result;
	}

}
