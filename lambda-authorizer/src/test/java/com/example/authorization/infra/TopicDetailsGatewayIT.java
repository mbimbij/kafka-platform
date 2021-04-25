package com.example.authorization.infra;

import com.example.authorization.core.Group;
import com.example.authorization.core.Topic;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TopicDetailsGatewayIT {

  @Test
  @Tag("manual")
  void getTopicDetails() {
    TopicDetailsGateway topicDetailsGateway = new TopicDetailsGateway();
    Topic expectedTopic = new Topic("topic1", new Group("group1"));

    Topic actualTopic = topicDetailsGateway.getTopicDetails("topic1");

    Assertions.assertThat(actualTopic).isEqualTo(expectedTopic);
  }
}