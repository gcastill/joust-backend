package com.joust.backend.data.spring;

import java.net.URI;
import java.util.Map;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.postgresql.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
public class DataConfiguration {
  
  @Autowired
  Environment environment;

  @Bean
  public URI dbUrl() throws Exception {
    return new URI(environment.getProperty("DATABASE_URL"));
  }

  @Bean
  @DependsOn("flyway")
  public DataSourceTransactionManager transactionManager() throws Exception {
    return new DataSourceTransactionManager(dataSource());
  }

  @Bean
  public DataSource dataSource() throws Exception {
    URI dbUrl = dbUrl();
    ComboPooledDataSource dataSource = new ComboPooledDataSource();
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
    flyway.clean();
    flyway.migrate();
    return flyway;
  }

  @Bean
  public Map<String, String> sqlFiles() throws Exception {
    FileToStringFactoryBean factory = new FileToStringFactoryBean();
    factory.setSuffix(".sql");
    factory.setLocation("classpath:/db/sql");
    return factory.createInstance();
  }

  @Bean
  @DependsOn("flyway")
  public JdbcTemplate jdbcTemplate() throws Exception {
    return new JdbcTemplate(dataSource());
  }

  @Bean
  @DependsOn("flyway")
  public NamedParameterJdbcTemplate namedParameterJdbcTemplate() throws Exception {
    return new NamedParameterJdbcTemplate(dataSource());
  }

  @Bean
  public JdbcUserProfileStore userProfileStore() throws Exception {
    JdbcUserProfileStore userProfileStore = new JdbcUserProfileStore();
    userProfileStore.setJdbcTemplate(namedParameterJdbcTemplate());
    userProfileStore.setMergeUserProfileSql(sqlFiles().get("MergeUserProfile.sql"));
    userProfileStore.setGetUserProfileByExternalSourceSql(sqlFiles().get("GetUserProfileByExternalSource.sql"));
    userProfileStore.setGetUserProfileSql(sqlFiles().get("GetUserProfile.sql"));
    userProfileStore.setSaveExternalProfileSourceSql(sqlFiles().get("SaveExternalProfileSource.sql"));
    userProfileStore.setRowMapper(new UserProfileRowMapper());
    return userProfileStore;
  }

}
