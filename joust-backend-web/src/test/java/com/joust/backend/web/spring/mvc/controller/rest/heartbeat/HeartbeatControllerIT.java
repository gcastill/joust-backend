package com.joust.backend.web.spring.mvc.controller.rest.heartbeat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;

import com.joust.backend.web.spring.mvc.controller.AbstractWebIT;

public class HeartbeatControllerIT extends AbstractWebIT {

  @Test
  public void getHeartbeatNoSecurity() throws Exception {
    withoutSecurity().perform(get("/rest/heartbeat").accept(MediaType.parseMediaType("application/json")))
        .andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.message").value("I'm here")).andExpect(jsonPath("$.version").value("master-SNAPSHOT"))
        .andExpect(jsonPath("$.buildLabel").value("20160719.011115"));
  }

  @Test
  public void getHeartbeatForbidden() throws Exception {
    withSecurity().perform(get("/rest/heartbeat").accept(MediaType.parseMediaType("application/json")))
        .andExpect(status().isUnauthorized());
  }

}
