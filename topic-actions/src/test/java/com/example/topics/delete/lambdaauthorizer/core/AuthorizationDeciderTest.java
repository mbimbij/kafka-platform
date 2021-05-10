package com.example.topics.delete.lambdaauthorizer.core;

import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import com.example.topics.core.User;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class AuthorizationDeciderTest {
  @Test
  void whenUserGroupEqualsTopicOwnerGroup_thenAuthorized() {
    // GIVEN
    AuthorizationDecider authorizationDecider = new AuthorizationDecider();
    Group group1 = new Group("group1");
    User userOfGroup1 = User.builder().groups(Collections.singletonList(group1)).build();
    Topic topicOfGroup1 = Topic.builder().ownerGroup(group1).build();

    // WHEN
    boolean isAuthorized = authorizationDecider.isDeletionAuthorized(userOfGroup1, topicOfGroup1);

    // THEN
    assertThat(isAuthorized).isTrue();
  }

  @Test
  void whenUserGroupNotEqualsTopicOwnerGroup_thenNotAuthorized() {
    // GIVEN
    AuthorizationDecider authorizationDecider = new AuthorizationDecider();
    Group group1 = new Group("group1");
    Group group2 = new Group("group2");
    User userOfGroup1 = User.builder().groups(Collections.singletonList(group1)).build();
    Topic topicOfGroup2 = Topic.builder().ownerGroup(group2).build();

    // WHEN
    boolean isAuthorized = authorizationDecider.isDeletionAuthorized(userOfGroup1, topicOfGroup2);

    // THEN
    assertThat(isAuthorized).isFalse();
  }
}