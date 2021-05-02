package com.example.topics.dao;

import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import com.example.topics.core.TopicDao;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

public class TopicDaoDynamoDbImpl implements TopicDao {
  private DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.create();
  public static final String TABLE_NAME = "topic-info";
  private DynamoDbTable<TopicEntity> topicInfoTable =
      enhancedClient.table(TABLE_NAME, TableSchema.fromBean(TopicEntity.class));

  @Override
  public void saveTopicInfo(Topic topic) {
    TopicEntity topicEntity = TopicEntity.builder()
        .topicName(topic.getName())
        .ownerGroup(topic.getOwnerGroup().getName())
        .build();

    topicInfoTable.putItem(topicEntity);
  }

  @Override
  public Optional<Topic> getTopicInfo(Topic topic) {
    String topicName = topic.getName();
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
