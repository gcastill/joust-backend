package com.joust.be.model.domain;

import java.net.URL;
import java.util.Locale;

public class User {

  public static enum UserSource {

    GOOGLE, FACEBOOK, INTERNAL;

  }

  private UserSource source;
  private String referenceId;
  private String email;
  private String givenName;
  private String familyName;
  private Locale locale;
  private URL profileUrl;

  public UserSource getSource() {
    return source;
  }

  public void setSource(UserSource userSource) {
    this.source = userSource;
  }

  public String getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public URL getProfileUrl() {
    return profileUrl;
  }

  public void setProfileUrl(URL profileUrl) {
    this.profileUrl = profileUrl;
  }

}
