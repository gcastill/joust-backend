package com.joust.backend.web.spring.mvc.controller.oauth.google;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.joust.backend.web.spring.Application;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class GoogleControllerFT {

  @Test
  public void test() {
    System.out.println(getClass().getName());
  }
}
