package com.joust.be.web.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Controller;

import com.joust.be.model.domain.Heartbeat;

@Controller
@Path("/heartbeat")
public class HeartbeatController {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getHeartbeat() {

    Heartbeat hb = new Heartbeat();
    hb.setMessage("I'm here");

    // TODO: hard coded for now.
    hb.setVersion("0.0.2");

    // TODO: hard coded for now.
    hb.setBuild("0.0.2-dev");
    
    
    return Response.ok(hb).build();
  }
}
