package com.example.topics.dao;

import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TopicDaoDynamoDbImplTest {
  private String TEST_TOPIC_NAME = UUID.randomUUID().toString();
  private TopicDaoDynamoDbImpl topicDaoDynamoDbImpl = new TopicDaoDynamoDbImpl();
  private Topic topic = Topic.builder()
      .name(TEST_TOPIC_NAME)
      .ownerGroup(new Group("myGroup"))
      .build();

  @BeforeEach
  void setUp() {
    topicDaoDynamoDbImpl.deleteTopicInfo(topic);
    assertThat(topicDaoDynamoDbImpl.getTopicInfo(topic)).isEmpty();
  }

  @AfterEach
  void tearDown() {
    topicDaoDynamoDbImpl.deleteTopicInfo(topic);
    assertThat(topicDaoDynamoDbImpl.getTopicInfo(topic)).isEmpty();
  }

  @Test
  @Tag("remote")
  void canCreateTopicInfoInRemoteDatabase() {
    // WHEN
    topicDaoDynamoDbImpl.saveTopicInfo(topic);

    // THEN
    Optional<Topic> topicInfoFromDatabase = topicDaoDynamoDbImpl.getTopicInfo(topic);
    assertThat(topicInfoFromDatabase).isNotEmpty();
    assertThat(topicInfoFromDatabase.get())
        .usingRecursiveComparison()
        .isEqualTo(topic);
  }
}