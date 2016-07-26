package com.joust.backend.web.spring.mvc.controller.rest.heartbeat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.joust.backend.core.model.Heartbeat;
import com.joust.backend.test.meanbean.BeanTesterBuilder;

@RunWith(MockitoJUnitRunner.class)
public class HeartbeatControllerTest {

  @Test
  public void testBean() {
    new BeanTesterBuilder().withFactory(Heartbeat.class, () -> Heartbeat.builder().build());

  }

}
