package com.joust.backend.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({ "classpath:beans-security.xml" })
public class SecurityConfiguration {

}
