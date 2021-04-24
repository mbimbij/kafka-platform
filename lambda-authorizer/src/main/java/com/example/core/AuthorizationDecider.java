package com.example.core;

import java.util.Collections;
import java.util.Optional;

public class AuthorizationDecider {

  public boolean isDeletionAuthorized(User user, Topic topic) {
    return Optional.ofNullable(user.getGroups())
        .orElse(Collections.emptyList())
        .contains(topic.getOwnerGroup());
  }
}
