package com.joust.backend.web.mvc;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

@Configuration
@ComponentScan(basePackages = "com.joust.backend.web.mvc")
public class MvcConfiguration extends WebMvcConfigurationSupport {

  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable();
  }

  @Bean
  public static PropertyPlaceholderConfigurer getPropertyPlaceholderConfigurer() {
    PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
    configurer.setLocation(new ClassPathResource("build.properties"));
    return configurer;
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

}
