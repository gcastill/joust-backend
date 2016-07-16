package com.joust.backend.data.spring;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meanbean.test.BeanTester;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FileToStringFactoryBeanTest {

  private BeanTester beanTester;

  @Before
  public void setup() throws Exception {
    beanTester = new BeanTester();

  }

  @Test
  public void testBean() {
    beanTester.testBean(FileToStringFactoryBean.class);
  }

}
