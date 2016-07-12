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
import com.joust.backend.core.model.UserProfile.UserProfileBuilder;

public class UserProfileRowMapper implements RowMapper<UserProfile> {

	@Override
	public UserProfile mapRow(ResultSet rs, int rowNum) throws SQLException {

		UserProfileBuilder builder = UserProfile.builder();

		builder.id(UUID.fromString(rs.getString(USER_PROFILE_ID)));

		builder.email(rs.getString(EMAIL));
		builder.givenName(rs.getString(GIVEN_NAME));
		builder.familyName(rs.getString(FAMILY_NAME));
		String profileUrlString = rs.getString(PROFILE_URL);
		try {
			builder.profileUrl(profileUrlString != null ? new URL(profileUrlString) : null);
		} catch (MalformedURLException e) {
			throw new SQLException(e.getMessage(), e);
		}
		String localeString = rs.getString(LOCALE);
		builder.locale(localeString != null ? Locale.forLanguageTag(localeString) : null);

		return builder.build();
	}

}
