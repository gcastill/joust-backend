package com.joust.backend.web.spring;

import org.apache.catalina.startup.Tomcat;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.joust.backend.web.spring.mvc.controller.oauth.google.GoogleControllerFT;
import com.joust.backend.web.spring.mvc.controller.rest.heartbeat.HeartbeatControllerFT;

import lombok.SneakyThrows;

@RunWith(Suite.class)
@SuiteClasses({ HeartbeatControllerFT.class, GoogleControllerFT.class, AppMainFT.class })
public class FunctionalSuite {

  @ClassRule
  public static ExternalResource server = new ExternalResource() {

    AppMain appMain = new AppMain(new Tomcat());

    @Override
    protected void before() throws Throwable {
      appMain.init().start();

    }

    @SneakyThrows
    protected void after() {
      appMain.stop().destroy();

    };

  };

}
