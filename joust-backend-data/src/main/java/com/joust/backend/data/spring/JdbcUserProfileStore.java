package com.joust.backend.data.spring;

import static com.joust.backend.data.spring.ExternalProfileSourceColumns.REFERENCE_ID;
import static com.joust.backend.data.spring.ExternalProfileSourceColumns.SOURCE;
import static com.joust.backend.data.spring.UserProfileColumns.EMAIL;
import static com.joust.backend.data.spring.UserProfileColumns.FAMILY_NAME;
import static com.joust.backend.data.spring.UserProfileColumns.GIVEN_NAME;
import static com.joust.backend.data.spring.UserProfileColumns.LOCALE;
import static com.joust.backend.data.spring.UserProfileColumns.PROFILE_URL;
import static com.joust.backend.data.spring.UserProfileColumns.USER_PROFILE_ID;

import java.sql.Types;
import java.util.Collections;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.joust.backend.core.data.UserProfileStore;
import com.joust.backend.core.model.ExternalProfileSource;
import com.joust.backend.core.model.ExternalProfileSource.Source;
import com.joust.backend.core.model.UserProfile;

public final class JdbcUserProfileStore implements UserProfileStore {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	private String mergeUserProfileSql;
	private String getUserProfileSql;
	private String getUserProfileByExternalSourceSql;
	private String saveExternalProfileSourceSql;
	private RowMapper<UserProfile> rowMapper;

	public JdbcUserProfileStore(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public UserProfile getUserProfile(String id) {
		return jdbcTemplate.queryForObject(getUserProfileSql, Collections.singletonMap(USER_PROFILE_ID, id), rowMapper);
	}

	@Override
	public void mergeUserProfile(UserProfile profile) {
		jdbcTemplate.update(mergeUserProfileSql, createParameterMap(profile));

	}

	@Override
	public UserProfile getUserProfileByExternalSource(Source source, String referenceId) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue(SOURCE, source, Types.VARCHAR).addValue(REFERENCE_ID, referenceId);
		return jdbcTemplate.query(getUserProfileByExternalSourceSql, map, rowMapper).stream().findFirst().orElse(null);
	}

	@Override
	public void saveExternalProfileSource(ExternalProfileSource externalProfileSource) {
		jdbcTemplate.update(saveExternalProfileSourceSql, createParameterMap(externalProfileSource));
	}

	MapSqlParameterSource createParameterMap(UserProfile profile) {
		return new MapSqlParameterSource().addValue(USER_PROFILE_ID, profile.getId())
				.addValue(EMAIL, profile.getEmail()).addValue(GIVEN_NAME, profile.getGivenName())
				.addValue(FAMILY_NAME, profile.getFamilyName()).addValue(LOCALE, profile.getLocale(), Types.VARCHAR)
				.addValue(PROFILE_URL, profile.getProfileUrl(), Types.VARCHAR);
	}

	private MapSqlParameterSource createParameterMap(ExternalProfileSource externalProfileSource) {
		return new MapSqlParameterSource().addValue(SOURCE, externalProfileSource.getSource(), Types.VARCHAR)
				.addValue(REFERENCE_ID, externalProfileSource.getReferenceId())
				.addValue(USER_PROFILE_ID, externalProfileSource.getUserProfileId());
	}

	public NamedParameterJdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public String getMergeUserProfileSql() {
		return mergeUserProfileSql;
	}

	public void setMergeUserProfileSql(String mergeUserProfileSql) {
		this.mergeUserProfileSql = mergeUserProfileSql;
	}

	public String getGetUserProfileSql() {
		return getUserProfileSql;
	}

	public void setGetUserProfileSql(String getUserProfileSql) {
		this.getUserProfileSql = getUserProfileSql;
	}

	public String getGetUserProfileByExternalSourceSql() {
		return getUserProfileByExternalSourceSql;
	}

	public void setGetUserProfileByExternalSourceSql(String getUserProfileByExternalSourceSql) {
		this.getUserProfileByExternalSourceSql = getUserProfileByExternalSourceSql;
	}

	public String getSaveExternalProfileSourceSql() {
		return saveExternalProfileSourceSql;
	}

	public void setSaveExternalProfileSourceSql(String saveExternalProfileSourceSql) {
		this.saveExternalProfileSourceSql = saveExternalProfileSourceSql;
	}

	public RowMapper<UserProfile> getRowMapper() {
		return rowMapper;
	}

	public void setRowMapper(RowMapper<UserProfile> rowMapper) {
		this.rowMapper = rowMapper;
	}

}
