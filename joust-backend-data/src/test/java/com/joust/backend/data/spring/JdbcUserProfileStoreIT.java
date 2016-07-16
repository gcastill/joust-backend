package com.joust.backend.data.spring;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration("classpath:beans-data.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class JdbcUserProfileStoreIT {

  @Resource
  JdbcUserProfileStore instance;

  @Test
  public void testBean() {
    System.out.println(instance.hashCode());
  }
}
