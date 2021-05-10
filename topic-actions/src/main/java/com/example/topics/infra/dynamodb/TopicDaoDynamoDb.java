package com.example.topics.infra.dynamodb;

import com.example.topics.core.Group;
import com.example.topics.core.TopicDatabaseInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class TopicDaoDynamoDb {
  public static final String TABLE_NAME = "topic-info";
  private final DynamoDbEnhancedClient enhancedClient;
  private final DynamoDbTable<TopicEntity> topicInfoTable;

  public void saveTopicInfo(TopicDatabaseInfo topicDatabaseInfo) {
    TopicEntity topicEntity = TopicEntity.builder()
        .topicName(topicDatabaseInfo.getName())
        .ownerGroup(topicDatabaseInfo.getOwnerGroup().getName())
        .build();

    topicInfoTable.putItem(topicEntity);
  }

  public Optional<TopicDatabaseInfo> getTopicInfo(String topicName) {
    TopicEntity topicEntity = topicInfoTable.getItem(Key.builder()
        .partitionValue(topicName)
        .build());
    return Optional.ofNullable(topicEntity)
        .map(topicEntity1 -> TopicDatabaseInfo.builder()
            .name(topicEntity1.getTopicName())
            .ownerGroup(new Group(topicEntity1.getOwnerGroup()))
            .build());
  }

  public void deleteTopicInfo(String topicName) {
    log.info("deleting topic: {}", topicName);
    topicInfoTable.deleteItem(Key.builder()
        .partitionValue(topicName)
        .build());
  }
}
