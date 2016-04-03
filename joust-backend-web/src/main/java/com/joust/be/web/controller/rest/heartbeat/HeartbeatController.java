package com.joust.be.web.controller.rest.heartbeat;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import com.joust.be.model.domain.Heartbeat;

@Component
@RequestMapping("/rest/heartbeat")
public class HeartbeatController {

  @Resource
  private String buildVersion;

  @Resource
  private String buildLabel;

  @RequestMapping(method = GET)
  public ResponseEntity<Heartbeat> getHeartbeat() {

    Heartbeat hb = new Heartbeat();
    hb.setMessage("I'm here");
    hb.setVersion(buildVersion);
    hb.setBuild(buildLabel);
    ResponseEntity<Heartbeat> response = new ResponseEntity<Heartbeat>(hb, HttpStatus.OK);
    return response;
  }

  public String getBuildLabel() {
    return buildLabel;
  }

  public void setBuildLabel(String buildLabel) {
    this.buildLabel = buildLabel;
  }

  public String getBuildVersion() {
    return buildVersion;
  }

  public void setBuildVersion(String buildVersion) {
    this.buildVersion = buildVersion;
  }
}
