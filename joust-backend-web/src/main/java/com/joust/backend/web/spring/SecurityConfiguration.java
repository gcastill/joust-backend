package com.joust.backend.web.spring;

import static java.util.Arrays.asList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.vote.ScopeVoter;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
@Configuration
@ImportResource({ "classpath:beans-security.xml" })
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private DataSource dataSource;

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http
        //
        .requestMatchers().antMatchers("/oauth/**", "/rest/**").and()

        //
        .userDetailsService(clientDetailsUserService())
        //
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        //
        .cors().disable()
        //
        .csrf().disable()
        //
        .anonymous().disable()
        //
        .httpBasic().authenticationEntryPoint(clientAuthenticationEntryPoint()).and()
        //
        .addFilterBefore(resourceServerFilter(tokenServices()), BasicAuthenticationFilter.class)
        //
        .addFilterAfter(clientCredentialsTokenEndpointFilter(), BasicAuthenticationFilter.class)
        //
        .authorizeRequests().accessDecisionManager(accessDecisionManager()).and()
        //
        .authorizeRequests().antMatchers("/oauth/token").fullyAuthenticated().and()
        //
        .authorizeRequests().antMatchers("/oauth/google").fullyAuthenticated().and()
        //
        .authorizeRequests().antMatchers("/rest/**").hasRole("ROLE_CLIENT").and()
        //
        .exceptionHandling().accessDeniedHandler(oauthAccessDeniedHandler())
        //
        .authenticationEntryPoint(oauthAuthenticationEntryPoint());

  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication().dataSource(dataSource);
  }

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

  @Bean
  public UnanimousBased accessDecisionManager() {
    UnanimousBased bean = new UnanimousBased(asList(new ScopeVoter(), new RoleVoter(), new AuthenticatedVoter()));
    return bean;
  }

  @Bean
  public OAuth2AuthenticationEntryPoint oauthAuthenticationEntryPoint() {
    OAuth2AuthenticationEntryPoint bean = new OAuth2AuthenticationEntryPoint();
    bean.setRealmName("test");
    return bean;
  }

  @Bean
  public OAuth2AuthenticationEntryPoint clientAuthenticationEntryPoint() {
    OAuth2AuthenticationEntryPoint bean = new OAuth2AuthenticationEntryPoint();
    bean.setTypeName("Basic");
    bean.setRealmName("test/client");
    return bean;
  }

  @Bean
  public OAuth2AccessDeniedHandler oauthAccessDeniedHandler() {
    return new OAuth2AccessDeniedHandler();
  }

  @Bean
  public JdbcUserDetailsManager userDetailsService() {
    JdbcUserDetailsManager bean = new JdbcUserDetailsManager();
    bean.setDataSource(dataSource);
    return bean;
  }

  @Bean
  public ProviderManager clientAuthenticationManager() {
    DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
    dao.setUserDetailsService(clientDetailsUserService());
    ProviderManager bean = new ProviderManager(asList(dao));
    return bean;
  }

  @Bean
  public ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter() {
    ClientCredentialsTokenEndpointFilter bean = new ClientCredentialsTokenEndpointFilter();
    bean.setAuthenticationManager(clientAuthenticationManager());
    return bean;
  }

  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
  public ProviderManager authenticationManager() {
    DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
    dao.setUserDetailsService(userDetailsService());
    ProviderManager bean = new ProviderManager(asList(dao));
    return bean;
  }

  @Bean
  public OAuth2AuthenticationProcessingFilter resourceServerFilter(ResourceServerTokenServices tokenServices) {

    OAuth2AuthenticationManager authManager = new OAuth2AuthenticationManager();
    authManager.setResourceId("test");
    authManager.setTokenServices(tokenServices);

    OAuth2AuthenticationProcessingFilter bean = new OAuth2AuthenticationProcessingFilter();
    bean.setAuthenticationManager(authManager);

    return bean;

  }

  @Bean
  public OAuth2MethodSecurityExpressionHandler oauthExpressionHandler() {
    return new OAuth2MethodSecurityExpressionHandler();
  }

  @Bean
  OAuth2WebSecurityExpressionHandler oauthWebExpressionHandler() {
    return new OAuth2WebSecurityExpressionHandler();
  }

}
