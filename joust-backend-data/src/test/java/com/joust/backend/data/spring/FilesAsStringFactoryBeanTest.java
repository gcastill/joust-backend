package com.joust.backend.data.spring;

import org.flywaydb.core.internal.util.Location;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meanbean.test.BeanTester;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FilesAsStringFactoryBeanTest {

  private BeanTester beanTester;

  @Before
  public void setup() throws Exception {
    beanTester = new BeanTester();
    beanTester.getFactoryCollection().addFactory(Location.class, () -> {
      return new Location("test");
    });

  }

  @Test
  public void testBean() {
    beanTester.testBean(FilesAsStringFactoryBean.class);
  }

}
