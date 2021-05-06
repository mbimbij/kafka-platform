package com.example.topics.infra.dao;

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
class TopicDaoDynamoDbImplLocalDockerIT extends BaseLocalDockerIT {

  private Group testGroup = new Group("myGroup");
  private TopicDatabaseInfo testTopicDatabaseInfo;

  @BeforeEach
  void setUpSub() {
    testTopicDatabaseInfo = TopicDatabaseInfo.builder().name(correlationId).ownerGroup(testGroup).build();
    topicDao.deleteTopicInfo(testTopicDatabaseInfo);
    assertThat(topicDao.getTopicInfo(testTopicDatabaseInfo.getName())).isEmpty();
  }

  @AfterEach
  void tearDown() {
    topicDao.deleteTopicInfo(testTopicDatabaseInfo);
    assertThat(topicDao.getTopicInfo(testTopicDatabaseInfo.getName())).isEmpty();
  }

  @Test
  void givenNoTopicInfoInDynamo_whenSaveTopicInfo_thenTopicInfoCreated() {
    // GIVEN
    assertThat(topicDao.getTopicInfo(correlationId)).isEmpty();

    // WHEN
    topicDao.saveTopicInfo(testTopicDatabaseInfo);

    // THEN
    assertThat(topicDao.getTopicInfo(correlationId)).hasValueSatisfying(topic -> Objects.equals(topic.getName(), correlationId));
  }
}