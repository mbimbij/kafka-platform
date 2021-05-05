package com.example.topics.details;

import com.amazonaws.services.lambda.runtime.Context;
import com.example.topics.sharedcore.Group;
import com.example.topics.sharedcore.Topic;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GetTopicDetailsHandlerTest {
  @Test
  void testMockVersion() {
    // GIVEN
    GetTopicDetailsHandler getTopicDetailsHandler = new GetTopicDetailsHandler();
    Topic expectedTopic = new Topic("topic1", new Group("group1"));

    // WHEN
    Topic actualTopic = getTopicDetailsHandler.handleRequest(Map.of("topicName", "topic1"), mock(Context.class));

    // THEN
    assertThat(actualTopic).isEqualTo(expectedTopic);
  }
}