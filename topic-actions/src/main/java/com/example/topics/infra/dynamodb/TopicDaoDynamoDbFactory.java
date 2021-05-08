package com.example.topics.infra.dynamodb;

import com.example.topics.infra.EnvironmentVariables;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.utils.StringUtils;

import java.net.URI;

public class TopicDaoDynamoDbFactory {
  public static TopicDaoDynamoDb buildTopicDaoDynamoDb() {
    DynamoDbEnhancedClient dynamoDbEnhancedClient = createDynamoDbEnhancedClient();
    DynamoDbTable<TopicEntity> dynamoDbTable = dynamoDbEnhancedClient.table(TopicDaoDynamoDb.TABLE_NAME, TableSchema.fromBean(TopicEntity.class));
    return new TopicDaoDynamoDb(dynamoDbEnhancedClient, dynamoDbTable);
  }

  public static DynamoDbEnhancedClient createDynamoDbEnhancedClient() {
    String dynamodbServiceUrlOverride = EnvironmentVariables.instance().get("DYNAMODB_SERVICE_URL_OVERRIDE");
    DynamoDbEnhancedClient dynamoDbEnhancedClient;
    if(StringUtils.isBlank(dynamodbServiceUrlOverride)){
      dynamoDbEnhancedClient = DynamoDbEnhancedClient.create();
    }else {
      dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
          .dynamoDbClient(createClientWithOverridenUrl(dynamodbServiceUrlOverride))
          .build();
    }
    return dynamoDbEnhancedClient;
  }

  protected static DynamoDbClient createClientWithOverridenUrl(String dynamodbServiceUrlOverride) {
    return DynamoDbClient.builder()
        .endpointOverride(URI.create(dynamodbServiceUrlOverride))
        .region(Region.EU_WEST_3)
        .build();
  }
}
