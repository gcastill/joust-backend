package com.joust.backend.web;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

@Configuration
@ImportResource({ "classpath:beans-security.xml" })
public class SecurityConfiguration {

  @Autowired
  private DataSource dataSource;

  @Bean
  public JdbcTokenStore tokenStore() {
    return new JdbcTokenStore(dataSource);
  }

  @Bean
  public JdbcClientDetailsService clientDetails() {
    return new JdbcClientDetailsService(dataSource);
  }

  @Bean
  public ClientDetailsUserDetailsService clientDetailsUserService() {
    return new ClientDetailsUserDetailsService(clientDetails());
  }

  @Bean
  public DefaultTokenServices tokenServices() {
    DefaultTokenServices bean = new DefaultTokenServices();
    bean.setTokenStore(tokenStore());
    bean.setSupportRefreshToken(true);
    bean.setClientDetailsService(clientDetails());
    return bean;
  }

  @Bean
  public TokenStoreUserApprovalHandler userApprovalHandler() {
    TokenStoreUserApprovalHandler bean = new TokenStoreUserApprovalHandler();
    bean.setClientDetailsService(clientDetails());
    bean.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetails()));
    bean.setTokenStore(tokenStore());
    return bean;
  }

}
