package com.joust.backend.web.spring.mvc;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.joust.backend.core.model.Heartbeat;

@Configuration
@ComponentScan(basePackages = "com.joust.backend.web.spring.mvc")
@PropertySource("classpath:build.properties")
public class MvcConfiguration extends WebMvcConfigurationSupport {

  @Bean
  public Heartbeat heartbeat(@Value("${build.version}") String buildVersion,
      @Value("${build.label}") String buildLabel) {
    return Heartbeat.builder().buildLabel(buildLabel).version(buildVersion).message("I'm here").build();
  }

  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable();
  }

  @Bean
  public HttpTransport googleHttpTransport() {
    return new NetHttpTransport();
  }

  @Bean
  public JsonFactory googleJsonFactory() {
    return new JacksonFactory();
  }

  @Bean
  public String googleClientId() {
    return "1085953861517-f98qb7d43294d9a0a809v6hf68iqliv9.apps.googleusercontent.com";
  }

  @Bean
  public String googleIssuer() {
    return "accounts.google.com";
  }

  @Bean
  public GoogleIdTokenVerifier googleIDTokenVerifier() {
    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(googleHttpTransport(), googleJsonFactory())
        .setAudience(Arrays.asList(googleClientId())).setIssuer(googleIssuer()).build();
    return verifier;
  }

}
