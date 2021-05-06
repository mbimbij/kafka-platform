package com.example.topics.infra.dynamodb;

import com.example.topics.BaseLocalDockerIT;
import com.example.topics.core.Group;
import com.example.topics.core.TopicDatabaseInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class TopicDaoDynamoDbLocalDockerIT extends BaseLocalDockerIT {

  private Group testGroup = new Group("myGroup");
  private TopicDatabaseInfo testTopicDatabaseInfo;

  @BeforeEach
  void setUpSub() {
    testTopicDatabaseInfo = TopicDatabaseInfo.builder().name(correlationId).ownerGroup(testGroup).build();
    topicDaoDynamoDb.deleteTopicInfo(testTopicDatabaseInfo.getName());
    assertThat(topicDaoDynamoDb.getTopicInfo(testTopicDatabaseInfo.getName())).isEmpty();
  }

  @AfterEach
  void tearDown() {
    topicDaoDynamoDb.deleteTopicInfo(testTopicDatabaseInfo.getName());
    assertThat(topicDaoDynamoDb.getTopicInfo(testTopicDatabaseInfo.getName())).isEmpty();
  }

  @Test
  void givenNoTopicInfoInDynamo_whenSaveTopicInfo_thenTopicInfoCreated() {
    // GIVEN
    assertThat(topicDaoDynamoDb.getTopicInfo(correlationId)).isEmpty();

    // WHEN
    topicDaoDynamoDb.saveTopicInfo(testTopicDatabaseInfo);

    // THEN
    assertThat(topicDaoDynamoDb.getTopicInfo(correlationId)).hasValueSatisfying(topic -> Objects.equals(topic.getName(), correlationId));
  }
}