package com.joust.backend.data.spring;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import com.joust.backend.core.model.ExternalProfileSource;
import com.joust.backend.core.model.UserProfile;
import com.joust.backend.core.model.ExternalProfileSource.Source;
import com.joust.backend.core.model.UserProfile.UserProfileBuilder;

import junit.framework.Assert;

@ContextConfiguration("classpath:beans-data.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class JdbcUserProfileStoreIT {

  @Resource
  private JdbcUserProfileStore instance;

  @Resource
  private DataSource dataSource;

  private JdbcTemplate jdbcTemplate;

  @Before
  public void before() {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Test
  public void testMergeUser() throws Exception {
    int userProfileStart = countRowsInTable("user_profile");
    UserProfile source = randomUser().build();
    instance.mergeUserProfile(source);
    assertEquals(userProfileStart + 1, countRowsInTable("user_profile"));

    instance.mergeUserProfile(source.toBuilder().email("mike@joust.com").build());
    assertEquals(userProfileStart + 1, countRowsInTable("user_profile"));

    instance.mergeUserProfile(source.toBuilder().id(UUID.randomUUID()).build());
    assertEquals(userProfileStart + 2, countRowsInTable("user_profile"));

  }

  @Test
  public void testGetUser() throws Exception {
    int userProfileStart = countRowsInTable("user_profile");
    UserProfile source = randomUser().build();
    instance.mergeUserProfile(source);
    assertEquals(userProfileStart + 1, countRowsInTable("user_profile"));

    UserProfile copy = instance.getUserProfile(source.getId());
    assertEquals(source, copy);

  }

  @Test
  public void testGetUserByExternalProfileSource() throws Exception {
    int userProfileStart = countRowsInTable("user_profile");
    UserProfile source = randomUser().build();
    instance.mergeUserProfile(source);
    assertEquals(userProfileStart + 1, countRowsInTable("user_profile"));

    int externalProfileSourceStart = countRowsInTable("external_profile_source");
    instance.saveExternalProfileSource(ExternalProfileSource.builder().referenceId("test").source(Source.GOOGLE)
        .userProfileId(source.getId()).build());
    assertEquals(externalProfileSourceStart + 1, countRowsInTable("external_profile_source"));

    UserProfile copy = instance.getUserProfileByExternalSource(Source.GOOGLE, "test");
    assertEquals(source, copy);

  }

  public UserProfileBuilder randomUser() throws Exception {
    return UserProfile.builder().id(UUID.randomUUID()).email("gustavo@joust.com").givenName("gustavo")
        .familyName("castillo").profileUrl(new URL("http://joustframework.com")).locale(Locale.forLanguageTag("en"));
  }

  public int countRowsInTable(String table) {
    return JdbcTestUtils.countRowsInTable(jdbcTemplate, table);
  }
}
