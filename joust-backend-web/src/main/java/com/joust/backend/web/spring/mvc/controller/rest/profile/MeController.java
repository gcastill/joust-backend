package com.joust.backend.web.spring.mvc.controller.rest.profile;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.joust.backend.core.data.UserProfileStore;
import com.joust.backend.core.model.UserProfile;

import lombok.Data;

@Controller
@RequestMapping("/rest/me")
@Data
public class MeController {

  @Resource
  private UserProfileStore store;

  @RequestMapping(method = GET)
  public ResponseEntity<UserProfile> get() {

    ResponseEntity<UserProfile> response = new ResponseEntity<UserProfile>(
        store.getUserProfile(UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName())),
        HttpStatus.OK);
    return response;
  }
}
