package com.joust.backend.data.spring;

import org.junit.Test;
import org.meanbean.test.BeanTester;

public class QueryStringFactoryBeanTest {

  @Test
  public void testBean() {
    new BeanTester().testBean(QueryStringFactoryBean.class);
  }
}
