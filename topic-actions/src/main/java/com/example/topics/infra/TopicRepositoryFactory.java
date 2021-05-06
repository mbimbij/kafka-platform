package com.example.topics.infra;

import com.example.topics.infra.dynamodb.TopicDaoDynamoDbFactory;
import com.example.topics.infra.kafka.KafkaClusterProxyFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TopicRepositoryFactory {

  public static TopicRepositoryImpl buildTopicRepositoryFactory() {
    return new TopicRepositoryImpl(KafkaClusterProxyFactory.buildKafkaClusterProxy(), TopicDaoDynamoDbFactory.buildTopicDaoDynamoDb());
  }

}
