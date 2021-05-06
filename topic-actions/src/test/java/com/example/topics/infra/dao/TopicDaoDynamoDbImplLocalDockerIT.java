package com.example.topics.infra.dao;

import com.example.topics.BaseLocalDockerIT;
import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class TopicDaoDynamoDbImplLocalDockerIT extends BaseLocalDockerIT {

  private Group testGroup = new Group("myGroup");
  private Topic testTopic;

  @BeforeEach
  void setUpSub() {
    testTopic = Topic.builder().name(correlationId).ownerGroup(testGroup).build();
    topicDaoDynamoDbImpl.deleteTopicInfo(testTopic);
    assertThat(topicDaoDynamoDbImpl.getTopicInfo(testTopic.getName())).isEmpty();
  }

  @AfterEach
  void tearDown() {
    topicDaoDynamoDbImpl.deleteTopicInfo(testTopic);
    assertThat(topicDaoDynamoDbImpl.getTopicInfo(testTopic.getName())).isEmpty();
  }

  @Test
  void givenNoTopicInfoInDynamo_whenSaveTopicInfo_thenTopicInfoCreated() {
    // GIVEN
    assertThat(topicDaoDynamoDbImpl.getTopicInfo(correlationId)).isEmpty();

    // WHEN
    topicDaoDynamoDbImpl.saveTopicInfo(testTopic);

    // THEN
    assertThat(topicDaoDynamoDbImpl.getTopicInfo(correlationId)).hasValueSatisfying(topic -> Objects.equals(topic.getName(), correlationId));
  }
}