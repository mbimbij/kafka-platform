package com.example.topics.infra.dynamodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TopicEntity {
  private String topicName;
  private String ownerGroup;

  @DynamoDbPartitionKey
  public String getTopicName() {
    return topicName;
  }
}
