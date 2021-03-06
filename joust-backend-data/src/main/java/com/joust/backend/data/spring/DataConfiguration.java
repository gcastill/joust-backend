package com.joust.backend.data.spring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.postgresql.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.jolbox.bonecp.BoneCPDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableTransactionManagement
@Configuration
public class DataConfiguration {

  @Autowired
  private Environment environment;

  @Bean
  public URI dbUrl() throws Exception {
    return new URI(environment.getProperty("DATABASE_URL"));
  }

  @Bean
  @DependsOn("flyway")
  public DataSourceTransactionManager txManager() throws Exception {
    return new DataSourceTransactionManager(dataSource());
  }

  @Bean(destroyMethod = "close")
  public DataSource dataSource() throws Exception {
    URI dbUrl = dbUrl();
    BoneCPDataSource dataSource = new BoneCPDataSource();

    dataSource.setDriverClass(Driver.class.getName());

    String dbUrlQuery = dbUrl.getQuery();
    dbUrlQuery = dbUrlQuery == null ? "" : "?" + dbUrlQuery;

    String jdbcUrl = String.format("jdbc:postgresql://%s:%s%s%s", dbUrl.getHost(), dbUrl.getPort(), dbUrl.getPath(),
        dbUrlQuery);

    String[] userInfo = dbUrl.getUserInfo().split(":");
    dataSource.setUser(userInfo[0]);
    dataSource.setPassword(userInfo[1]);

    dataSource.setJdbcUrl(jdbcUrl);

    return dataSource;
  }

  @Bean
  public Flyway flyway() throws Exception {
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource());
    if (environment.getProperty("joust.drop-database", Boolean.class, false)) {
      log.warn("dropping the database");
      flyway.clean();
    }
    flyway.migrate();
    return flyway;
  }

  @Bean
  @DependsOn("flyway")
  public NamedParameterJdbcTemplate namedParameterJdbcTemplate() throws Exception {
    return new NamedParameterJdbcTemplate(dataSource());
  }

  public JdbcUserProfileStore.SqlConfig userProfileSqlConfig() throws IOException {
    return JdbcUserProfileStore.SqlConfig.builder()
        .getUserProfileByExternalSourceSql(
            resourceToString("classpath:/db/sql/user-profile/GetUserProfileByExternalSource.sql"))
        .getUserProfileSql(resourceToString("classpath:/db/sql/user-profile/GetUserProfile.sql"))
        .mergeUserProfileSql(resourceToString("classpath:/db/sql/user-profile/MergeUserProfile.sql"))
        .saveExternalProfileSourceSql(resourceToString("classpath:/db/sql/user-profile/SaveExternalProfileSource.sql"))
        .searchUserProfilesSql(resourceToString("classpath:/db/sql/user-profile/UserProfileSearch.sql")).build();
  }

  @Bean
  public JdbcUserProfileStore userProfileStore() throws Exception {
    return JdbcUserProfileStore.builder().jdbcTemplate(namedParameterJdbcTemplate()).sql(userProfileSqlConfig())
        .rowMapper(new UserProfileRowMapper()).build();
  }

  private String resourceToString(String resourcePattern) throws IOException {
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    Resource resource = resolver.getResource(resourcePattern);
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
      return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

  }
}
