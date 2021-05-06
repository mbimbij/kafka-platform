package com.example.topics.infra.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class TopicDaoDynamoDbFactory {
  public static TopicDaoDynamoDb buildTopicDaoDynamoDb() {
    DynamoDbEnhancedClient dynamoDbEnhancedClient = createDynamoDbEnhancedClient();
    DynamoDbTable<TopicEntity> dynamoDbTable = dynamoDbEnhancedClient.table(TopicDaoDynamoDb.TABLE_NAME, TableSchema.fromBean(TopicEntity.class));
    return new TopicDaoDynamoDb(dynamoDbEnhancedClient, dynamoDbTable);
  }

  public static DynamoDbEnhancedClient createDynamoDbEnhancedClient() {
    return DynamoDbEnhancedClient.create();
  }
}
