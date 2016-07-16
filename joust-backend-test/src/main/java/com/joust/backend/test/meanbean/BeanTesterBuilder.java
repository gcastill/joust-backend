package com.joust.backend.test.meanbean;

import java.util.HashMap;
import java.util.Map;

import org.meanbean.factories.FactoryCollection;
import org.meanbean.lang.Factory;
import org.meanbean.test.BeanTester;

public class BeanTesterBuilder {

  Map<Class<?>, Factory<?>> factories = new HashMap<>();

  public <T> BeanTesterBuilder withFactory(Class<T> clazz, Factory<T> factory) {
    factories.put(clazz, factory);
    return this;
  }

  public BeanTester build() {
    BeanTester result = new BeanTester();
    FactoryCollection factoryCollection = result.getFactoryCollection();
    factories.entrySet().forEach(entry -> {
      factoryCollection.addFactory(entry.getKey(), entry.getValue());
    });

    return result;
  }

}
