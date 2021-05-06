package com.example.topics.infra.dynamodb;

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

class TopicDaoDynamoDbRemoteIT {
  private String TEST_TOPIC_NAME = UUID.randomUUID().toString();
  private TopicDaoDynamoDb topicDaoDynamoDb;

  {
    DynamoDbEnhancedClient enhancedClient = TopicDaoDynamoDbFactory.createDynamoDbEnhancedClient();
    DynamoDbTable<TopicEntity> dynamoDbTable = enhancedClient.table(TopicDaoDynamoDb.TABLE_NAME, TableSchema.fromBean(TopicEntity.class));
    topicDaoDynamoDb = new TopicDaoDynamoDb(enhancedClient, dynamoDbTable);
  }

  private TopicDatabaseInfo topicDatabaseInfo = TopicDatabaseInfo.builder()
      .name(TEST_TOPIC_NAME)
      .ownerGroup(new Group("myGroup"))
      .build();

  @BeforeEach
  void setUp() {
    topicDaoDynamoDb.deleteTopicInfo(topicDatabaseInfo.getName());
    assertThat(topicDaoDynamoDb.getTopicInfo(topicDatabaseInfo.getName())).isEmpty();
  }

  @AfterEach
  void tearDown() {
    topicDaoDynamoDb.deleteTopicInfo(topicDatabaseInfo.getName());
    assertThat(topicDaoDynamoDb.getTopicInfo(topicDatabaseInfo.getName())).isEmpty();
  }

  @Test
  @Tag("remote")
  void canCreateTopicInfoInRemoteDatabase() {
    // WHEN
    topicDaoDynamoDb.saveTopicInfo(topicDatabaseInfo);

    // THEN
    Optional<TopicDatabaseInfo> topicInfoFromDatabase = topicDaoDynamoDb.getTopicInfo(topicDatabaseInfo.getName());
    assertThat(topicInfoFromDatabase).isNotEmpty();
    assertThat(topicInfoFromDatabase.get())
        .usingRecursiveComparison()
        .isEqualTo(topicDatabaseInfo);
  }

}