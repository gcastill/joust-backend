package com.joust.backend.web.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.joust.backend.data.spring.DataConfiguration;

@SpringBootApplication
@Import({ DataConfiguration.class, WebConfiguration.class, SecurityConfiguration.class })
public class Application {

  public static void main(String[] args) {
    System.setProperty("server.port", System.getenv().getOrDefault("PORT", "8080"));
    SpringApplication.run(Application.class, args);
  }

}
