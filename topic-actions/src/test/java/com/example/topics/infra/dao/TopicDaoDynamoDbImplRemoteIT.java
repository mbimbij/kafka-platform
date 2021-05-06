package com.example.topics.infra.dao;

import com.example.topics.core.Group;
import com.example.topics.core.TopicDatabaseInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TopicDaoDynamoDbImplRemoteIT {
  private String TEST_TOPIC_NAME = UUID.randomUUID().toString();
  private TopicDaoDynamoDbImpl topicDaoDynamoDbImpl;

  {
    DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClientFactory.createDynamoDbEnhancedClient();
    DynamoDbTable<TopicEntity> dynamoDbTable = enhancedClient.table(TopicDaoDynamoDbImpl.TABLE_NAME, TableSchema.fromBean(TopicEntity.class));
    topicDaoDynamoDbImpl = new TopicDaoDynamoDbImpl(enhancedClient, dynamoDbTable);
  }

  private TopicDatabaseInfo topicDatabaseInfo = TopicDatabaseInfo.builder()
      .name(TEST_TOPIC_NAME)
      .ownerGroup(new Group("myGroup"))
      .build();

  @BeforeEach
  void setUp() {
    topicDaoDynamoDbImpl.deleteTopicInfo(topicDatabaseInfo);
    assertThat(topicDaoDynamoDbImpl.getTopicInfo(topicDatabaseInfo.getName())).isEmpty();
  }

  @AfterEach
  void tearDown() {
    topicDaoDynamoDbImpl.deleteTopicInfo(topicDatabaseInfo);
    assertThat(topicDaoDynamoDbImpl.getTopicInfo(topicDatabaseInfo.getName())).isEmpty();
  }

  @Test
  @Tag("remote")
  void canCreateTopicInfoInRemoteDatabase() {
    // WHEN
    topicDaoDynamoDbImpl.saveTopicInfo(topicDatabaseInfo);

    // THEN
    Optional<TopicDatabaseInfo> topicInfoFromDatabase = topicDaoDynamoDbImpl.getTopicInfo(topicDatabaseInfo.getName());
    assertThat(topicInfoFromDatabase).isNotEmpty();
    assertThat(topicInfoFromDatabase.get())
        .usingRecursiveComparison()
        .isEqualTo(topicDatabaseInfo);
  }

}