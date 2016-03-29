package com.joust.be.web.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import io.swagger.jaxrs.config.BeanConfig;

public class SwaggerApplication extends HttpServlet {

  private static final long serialVersionUID = -6039834823506457822L;

  @Override
  public void init(ServletConfig config) throws ServletException {

    super.init(config);

    BeanConfig beanConfig = new BeanConfig();

    final String contextPath = config.getServletContext().getContextPath();
    final StringBuilder sbBasePath = new StringBuilder();
    sbBasePath.append(contextPath);
    sbBasePath.append("/rest");
    beanConfig.setBasePath(sbBasePath.toString());

    // API Info
    beanConfig.setVersion("0.1");
    beanConfig.setTitle("Joust");
    beanConfig.setResourcePackage("com.joust.be.web.controller");
    beanConfig.setScan(true);

  }
}