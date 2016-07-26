package com.joust.backend.web.spring.mvc.controller.rest.heartbeat;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.annotation.Resource;

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

  @Resource
  private Heartbeat heartbeat;

  @RequestMapping(method = GET)
  public ResponseEntity<Heartbeat> getHeartbeat() {
    ResponseEntity<Heartbeat> response = new ResponseEntity<Heartbeat>(heartbeat.toBuilder().build(), HttpStatus.OK);
    return response;
  }
}
