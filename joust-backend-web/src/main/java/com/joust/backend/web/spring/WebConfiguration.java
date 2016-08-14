package com.joust.backend.web.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SecurityConfiguration.class)
public class WebConfiguration {

}
