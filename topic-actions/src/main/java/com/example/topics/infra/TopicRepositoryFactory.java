package com.example.topics.infra;

import com.example.topics.core.TopicRepository;
import com.example.topics.infra.dynamodb.TopicDaoDynamoDbFactory;
import com.example.topics.infra.kafka.KafkaClusterProxyFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TopicRepositoryFactory {

  private static TopicRepositoryFactory instance;

  private TopicRepositoryFactory() {
  }

  public TopicRepository buildTopicRepositoryFactory() {
    return new TopicRepositoryImpl(KafkaClusterProxyFactory.getInstance().buildKafkaClusterProxy(), TopicDaoDynamoDbFactory.getInstance().buildTopicDaoDynamoDb());
  }

  public static TopicRepositoryFactory getInstance() {
    if(instance == null){
      instance = new TopicRepositoryFactory();
    }
    return instance;
  }

  public static void setInstance(TopicRepositoryFactory instance) {
    TopicRepositoryFactory.instance = instance;
  }
}
