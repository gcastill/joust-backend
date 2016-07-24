package com.joust.backend.web.spring;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import com.joust.backend.data.spring.DataConfiguration;
import com.joust.backend.web.spring.mvc.MvcConfiguration;

public class JoustWebInitializer implements WebApplicationInitializer {

  @Override
  public void onStartup(ServletContext container) {

    container.setInitParameter("contextClass", AnnotationConfigWebApplicationContext.class.getName());
    container.setInitParameter("contextConfigLocation",
        Stream.of(WebConfiguration.class, DataConfiguration.class, SecurityConfiguration.class).map(Class::getName)
            .collect(Collectors.joining(" ")));
    container.addListener(new ContextLoaderListener());
    container.addListener(new RequestContextListener());

    ServletRegistration.Dynamic servlet = container.addServlet("mvc", new DispatcherServlet());
    servlet.setInitParameter("contextClass", AnnotationConfigWebApplicationContext.class.getName());
    servlet.setInitParameter("contextConfigLocation", MvcConfiguration.class.getName());

    servlet.setLoadOnStartup(1);
    servlet.addMapping("/");

    container.addFilter("springSecurityFilterChain", new DelegatingFilterProxy()).addMappingForUrlPatterns(null, false,
        "/*");

  }

}
