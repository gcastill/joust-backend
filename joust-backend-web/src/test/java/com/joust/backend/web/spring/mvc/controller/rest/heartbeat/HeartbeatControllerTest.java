package com.joust.backend.web.spring.mvc.controller.rest.heartbeat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meanbean.test.BeanTester;
import org.mockito.runners.MockitoJUnitRunner;

import com.joust.backend.web.spring.mvc.controller.rest.heartbeat.HeartbeatController;

@RunWith(MockitoJUnitRunner.class)
public class HeartbeatControllerTest {

  @Test
  public void testBean() {
    new BeanTester().testBean(HeartbeatController.class);

  }

}
