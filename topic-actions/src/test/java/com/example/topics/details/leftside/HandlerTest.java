package com.example.topics.details.leftside;

import com.amazonaws.services.lambda.runtime.Context;
import com.example.topics.details.core.Group;
import com.example.topics.details.core.Topic;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HandlerTest {
  @Test
  void name() {
    // GIVEN
    Handler handler = new Handler();
    Topic expectedTopic = new Topic("topic1", new Group("group1"));

    // WHEN
    Topic actualTopic = handler.handleRequest(Map.of("topicName", "topic1"), mock(Context.class));

    // THEN
    assertThat(actualTopic).isEqualTo(expectedTopic);
  }
}