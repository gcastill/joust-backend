package com.joust.backend.data.spring;

import java.net.URI;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QueryStringFactoryBean extends AbstractFactoryBean<String> {

  private URI databaseUri;

  @Override
  protected String createInstance() throws Exception {
    String query = databaseUri.getQuery();
    return query == null ? "" : "?" + query;
  }

  @Override
  public Class<?> getObjectType() {
    return String.class;
  }

}
