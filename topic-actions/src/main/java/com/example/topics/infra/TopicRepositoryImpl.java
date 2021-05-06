package com.example.topics.infra;

import com.example.topics.core.*;
import com.example.topics.infra.dynamodb.TopicDaoDynamoDb;
import com.example.topics.infra.kafka.KafkaClusterProxy;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class TopicRepositoryImpl implements TopicRepository {

  private final KafkaClusterProxy kafkaClusterProxy;
  private final TopicDaoDynamoDb topicDao;

  @Override
  public void create(Topic topic) {
    String topicName = topic.getName();
    String ownerGroupName = topic.getOwnerGroup().getName();
    kafkaClusterProxy.createKafkaCluster(topicName, this);
    TopicDatabaseInfo topicDatabaseInfo = TopicDatabaseInfo.builder().name(topicName).ownerGroup(topic.getOwnerGroup()).build();
//    TopicEntity topicEntity = TopicEntity.builder().topicName(topicName).ownerGroup(ownerGroupName).build();
    topicDao.saveTopicInfo(topicDatabaseInfo);
  }

  @Override
  public Optional<Topic> get(String topicName) {
    boolean topicExistsInKafkaCluster = kafkaClusterProxy.topicExistsInKafkaCluster(this);
    Optional<TopicDatabaseInfo> topicDatabaseInfo = topicDao.getTopicInfo(topicName);
    validateKafkaClusterAndDatabaseAreConsistent(topicName, topicExistsInKafkaCluster, topicDatabaseInfo);
    return topicDatabaseInfo.map(databaseInfo -> Topic.builder().name(databaseInfo.getName()).ownerGroup(databaseInfo.getOwnerGroup()).build());
  }

  private void validateKafkaClusterAndDatabaseAreConsistent(String topicName, boolean topicExistsInKafkaCluster, Optional<TopicDatabaseInfo> topicDatabaseInfo) {
    if (topicDatabaseInfo.isEmpty() && topicExistsInKafkaCluster) {
      throw new IllegalStateException("topic \"" + topicName + "\" exists in Kafka cluster but not in the database");
    } else if (topicDatabaseInfo.isPresent() && !topicExistsInKafkaCluster) {
      throw new IllegalStateException("topic \"" + topicName + "\" exists in database but not in Kafka cluster");
    }
  }

  @Override
  public void delete(String topicName) {
    topicDao.deleteTopicInfo(topicName);
  }

}
