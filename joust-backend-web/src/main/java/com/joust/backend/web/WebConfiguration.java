package com.joust.backend.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import com.joust.backend.data.spring.DataConfiguration;

@Configuration
@Import(DataConfiguration.class)
@ImportResource({ "classpath:beans-security.xml" })
public class WebConfiguration {

}
