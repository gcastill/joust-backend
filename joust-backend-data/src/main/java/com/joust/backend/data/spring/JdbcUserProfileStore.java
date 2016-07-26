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
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.joust.backend.core.data.UserProfileStore;
import com.joust.backend.core.model.ExternalProfileSource;
import com.joust.backend.core.model.ExternalProfileSource.Source;
import com.joust.backend.core.model.UserProfile;

import lombok.Builder;
import lombok.NonNull;


@Builder(toBuilder = true)
@Transactional
public final class JdbcUserProfileStore implements UserProfileStore {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final SqlConfig sql;
  private final RowMapper<UserProfile> rowMapper;

  @Override
  public UserProfile getUserProfile(@NonNull UUID id) {
    return jdbcTemplate.queryForObject(sql.getUserProfileSql, Collections.singletonMap(USER_PROFILE_ID, id.toString()),
        rowMapper);
  }

  @Override
  public void mergeUserProfile(@NonNull UserProfile profile) {
    jdbcTemplate.update(sql.mergeUserProfileSql, createParameterMap(profile));

  }

  @Override
  public UserProfile getUserProfileByExternalSource(Source source, String referenceId) {
    MapSqlParameterSource map = new MapSqlParameterSource();
    map.addValue(SOURCE, source, Types.VARCHAR).addValue(REFERENCE_ID, referenceId);
    return jdbcTemplate.query(sql.getUserProfileByExternalSourceSql, map, rowMapper).stream().findFirst().orElse(null);
  }

  @Override
  public void saveExternalProfileSource(ExternalProfileSource externalProfileSource) {
    jdbcTemplate.update(sql.saveExternalProfileSourceSql, createParameterMap(externalProfileSource));
  }

  @Override
  public List<UserProfile> searchUserProfiles(UserProfile example) {
    return jdbcTemplate.query(sql.searchUserProfilesSql, createParameterMap(example), rowMapper);
  }

  MapSqlParameterSource createParameterMap(UserProfile profile) {
    return new MapSqlParameterSource().addValue(USER_PROFILE_ID, profile.getId(), Types.VARCHAR)
        .addValue(EMAIL, profile.getEmail(), Types.VARCHAR).addValue(GIVEN_NAME, profile.getGivenName(), Types.VARCHAR)
        .addValue(FAMILY_NAME, profile.getFamilyName(), Types.VARCHAR)
        .addValue(LOCALE, profile.getLocale(), Types.VARCHAR)
        .addValue(PROFILE_URL, profile.getProfileUrl(), Types.VARCHAR);
  }

  private MapSqlParameterSource createParameterMap(ExternalProfileSource externalProfileSource) {
    return new MapSqlParameterSource().addValue(SOURCE, externalProfileSource.getSource(), Types.VARCHAR)
        .addValue(REFERENCE_ID, externalProfileSource.getReferenceId())
        .addValue(USER_PROFILE_ID, externalProfileSource.getUserProfileId());
  }

  
  @Builder
  public static class SqlConfig {
    private final String mergeUserProfileSql;
    private final String getUserProfileSql;
    private final String getUserProfileByExternalSourceSql;
    private final String saveExternalProfileSourceSql;
    private final String searchUserProfilesSql;
  }

}
