package com.joust.backend.web.spring;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

@Configuration
public class OAuth2ServerConfiguration {

  private static final String JOUST_RESOURCE_ID = "joust";

  @Configuration
  @EnableResourceServer
  protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
      resources.resourceId(JOUST_RESOURCE_ID).stateless(false);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

      http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
          //
          .requestMatchers()
          //
          .antMatchers("/oauth/**", "/rest/**").and()
          //
          .authorizeRequests()
          //
          .antMatchers("/oauth/token").fullyAuthenticated().antMatchers("/oauth/google").fullyAuthenticated()
          .antMatchers("/rest/**").hasRole("CLIENT").and()
          //
          .cors().disable()
          //
          .csrf().disable()
          //
          .anonymous().disable();
      //

    }

  }

  @Configuration
  @EnableAuthorizationServer
  protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserApprovalHandler userApprovalHandler;

    @Autowired
    private DataSource dataSource;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
      clients.jdbc(dataSource);

    }

    @Bean
    public TokenStore tokenStore() {
      return new JdbcTokenStore(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
      endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
          .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
      oauthServer.realm("joust/client");
    }

  }

}
