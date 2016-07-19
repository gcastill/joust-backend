package com.joust.backend.web.mvc.controller.rest.heartbeat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.joust.backend.web.mvc.MvcConfiguration;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextHierarchy({ @ContextConfiguration(locations = { "classpath:beans-web.xml", "classpath:beans-security.xml",
    "classpath:beans-data.xml" }), @ContextConfiguration(classes = MvcConfiguration.class) })
public class HeartbeatControllerIT {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @Before
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  public void getHeartbeat() throws Exception {
    this.mockMvc.perform(get("/rest/heartbeat").accept(MediaType.parseMediaType("application/json")))
        .andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.message").value("I'm here")).andExpect(jsonPath("$.version").value("master-SNAPSHOT"))
        .andExpect(jsonPath("$.buildLabel").value("20160719.011115"));
  }

}
