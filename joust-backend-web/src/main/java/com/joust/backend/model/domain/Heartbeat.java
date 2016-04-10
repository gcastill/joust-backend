package com.joust.backend.model.domain;

public class Heartbeat {

  private String message;
  private String version;
  private String build;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setBuild(String build) {
    this.build = build;
  }

  public String getBuild() {
    return build;
  }

}
