package com.joust.backend.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.joust.backend.data.spring.DataConfiguration;

@Configuration
@Import({ DataConfiguration.class, SecurityConfiguration.class })
public class WebConfiguration {

}
