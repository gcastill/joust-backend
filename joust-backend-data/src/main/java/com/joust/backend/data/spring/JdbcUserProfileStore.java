package com.joust.backend.data.spring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
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
		return jdbcTemplate.queryForObject(getUserProfileSql, Collections.singletonMap("ID", id), rowMapper);
	}

	@Override
	public void mergeUserProfile(UserProfile profile) {
		jdbcTemplate.update(mergeUserProfileSql, createParameterMap(profile));

	}

	@Override
	public UserProfile getUserProfileByExternalSource(Source source, String referenceId) {
		Map<String, Object> map = new HashMap<>();
		map.put("SOURCE", source);
		map.put("REFERENCE_ID", referenceId);
		return jdbcTemplate.queryForObject(getUserProfileByExternalSourceSql, map, rowMapper);
	}

	@Override
	public void saveExternalProfileSource(ExternalProfileSource externalProfileSource) {
		jdbcTemplate.update(saveExternalProfileSourceSql, createParameterMap(externalProfileSource));
	}

	Map<String, Object> createParameterMap(UserProfile profile) {
		Map<String, Object> map = new HashMap<>();
		map.put("ID", profile.getId().toString());
		return map;
	}

	private Map<String, Object> createParameterMap(ExternalProfileSource externalProfileSource) {
		Map<String, Object> map = new HashMap<>();
		map.put("SOURCE", externalProfileSource.getSource());
		map.put("REFERENCE_ID", externalProfileSource.getReferenceId());
		map.put("USER_PROFILE_ID", externalProfileSource.getUserProfileId());
		return map;
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
