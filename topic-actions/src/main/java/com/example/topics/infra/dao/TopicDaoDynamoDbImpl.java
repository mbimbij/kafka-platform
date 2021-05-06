package com.example.topics.infra.dao;

import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import com.example.topics.core.TopicDao;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

public class TopicDaoDynamoDbImpl implements TopicDao {
  public static final String TABLE_NAME = "topic-info";
  private DynamoDbEnhancedClient enhancedClient;
  private DynamoDbTable<TopicEntity> topicInfoTable;

  TopicDaoDynamoDbImpl(DynamoDbEnhancedClient dynamoDbEnhancedClient, DynamoDbTable<TopicEntity> topicInfoTable) {
    enhancedClient = dynamoDbEnhancedClient;
    this.topicInfoTable = topicInfoTable;
  }

  @Override
  public void saveTopicInfo(Topic topic) {
    TopicEntity topicEntity = TopicEntity.builder()
        .topicName(topic.getName())
        .ownerGroup(topic.getOwnerGroup().getName())
        .build();

    topicInfoTable.putItem(topicEntity);
  }

  @Override
  public Optional<Topic> getTopicInfo(String topicName) {
    TopicEntity topicEntity = topicInfoTable.getItem(Key.builder()
        .partitionValue(topicName)
        .build());
    return Optional.ofNullable(topicEntity)
        .map(topicEntity1 -> Topic.builder()
            .name(topicEntity1.getTopicName())
            .ownerGroup(new Group(topicEntity1.getOwnerGroup()))
            .build());
  }

  @Override
  public void deleteTopicInfo(Topic topic) {
    String topicName = topic.getName();
    topicInfoTable.deleteItem(Key.builder()
        .partitionValue(topicName)
        .build());
  }
}
