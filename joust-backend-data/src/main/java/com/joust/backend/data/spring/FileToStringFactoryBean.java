package com.joust.backend.data.spring;

import java.util.HashMap;
import java.util.Map;

import org.flywaydb.core.internal.util.Location;
import org.flywaydb.core.internal.util.scanner.Resource;
import org.flywaydb.core.internal.util.scanner.classpath.ClassPathScanner;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Setter
public class FileToStringFactoryBean extends AbstractFactoryBean<Map<String, String>> {

  private String location = "";

  private String prefix = "";
  private String suffix = "";

  @Override
  protected Map<String, String> createInstance() throws Exception {

    ClassPathScanner scanner = new ClassPathScanner(getClass().getClassLoader());
    Resource[] resources = scanner.scanForResources(new Location(location), prefix, suffix);
    Map<String, String> result = new HashMap<>();
    for (Resource resource : resources) {
      log.info("found {}", resource.getLocation());
      result.put(resource.getFilename(), resource.loadAsString("utf-8"));
    }

    return result;
  }

  @Override
  public Class<?> getObjectType() {
    return Map.class;
  }

}
