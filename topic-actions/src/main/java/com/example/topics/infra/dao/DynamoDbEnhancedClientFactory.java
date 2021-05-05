package com.example.topics.infra.dao;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

public class DynamoDbEnhancedClientFactory {
  public static DynamoDbEnhancedClient createDynamoDbEnhancedClient() {
    return DynamoDbEnhancedClient.create();
  }
}
