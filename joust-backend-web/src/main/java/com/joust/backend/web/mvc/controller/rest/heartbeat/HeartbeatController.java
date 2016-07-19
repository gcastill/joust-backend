package com.joust.backend.web.mvc.controller.rest.heartbeat;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.joust.backend.core.model.Heartbeat;

import lombok.Getter;
import lombok.Setter;

@Controller
@RequestMapping("/rest/heartbeat")
@Getter
@Setter
public class HeartbeatController {

  @Value("${build.version}")
  private String buildVersion;

  @Value("${build.label}")
  private String buildLabel;

  @RequestMapping(method = GET)
  public ResponseEntity<Heartbeat> getHeartbeat() {

    Heartbeat hb = Heartbeat.builder().buildLabel(buildLabel).version(buildVersion).message("I'm here").build();
    ResponseEntity<Heartbeat> response = new ResponseEntity<Heartbeat>(hb, HttpStatus.OK);
    return response;
  }
}
