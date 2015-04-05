package com.joust.be.web.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Controller;

import com.joust.be.model.domain.Heartbeat;

@Controller
@Path("/heartbeat")
public class HeartbeatController {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Heartbeat getHeartbeat() {
    Heartbeat hb = new Heartbeat();
    hb.setMessage("Hello, World!");
    return hb;
  }
}
