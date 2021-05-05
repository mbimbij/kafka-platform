package com.example.topics.infra.dao;

import com.example.topics.sharedcore.TopicDao;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class TopicDaoFactory {
  public static TopicDao buildTopicDao(){
    DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClientFactory.createDynamoDbEnhancedClient();
    DynamoDbTable<TopicEntity> dynamoDbTable = dynamoDbEnhancedClient.table(TopicDaoDynamoDbImpl.TABLE_NAME, TableSchema.fromBean(TopicEntity.class));
    return new TopicDaoDynamoDbImpl(dynamoDbEnhancedClient, dynamoDbTable);
  }
}
