package com.example.core;

import java.util.Optional;

public class AuthorizationDecider {

  public boolean isDeletionAuthorized(User user, Topic topic) {
    return Optional.ofNullable(user.getGroups())
        .map(groups -> groups.contains(topic.getOwnerGroup()))
        .orElse(false);
  }
}
