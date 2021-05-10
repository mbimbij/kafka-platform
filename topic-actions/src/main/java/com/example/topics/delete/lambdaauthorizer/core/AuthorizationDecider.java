package com.example.topics.delete.lambdaauthorizer.core;

import com.example.topics.core.Topic;
import com.example.topics.core.User;

import java.util.Optional;

public class AuthorizationDecider {

  public boolean isDeletionAuthorized(User user, Topic topic) {
    return Optional.ofNullable(user.getGroups())
        .map(groups -> groups.contains(topic.getOwnerGroup()))
        .orElse(false);
  }
}
