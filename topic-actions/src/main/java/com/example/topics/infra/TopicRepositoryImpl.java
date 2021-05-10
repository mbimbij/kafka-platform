package com.example.topics.infra;

import com.example.topics.core.Topic;
import com.example.topics.core.TopicDatabaseInfo;
import com.example.topics.core.TopicRepository;
import com.example.topics.infra.dynamodb.TopicDaoDynamoDb;
import com.example.topics.infra.kafka.KafkaClusterProxy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Getter
public class TopicRepositoryImpl implements TopicRepository {

  private final KafkaClusterProxy kafkaClusterProxy;
  private final TopicDaoDynamoDb topicDaoDynamoDb;

  @Override
  public void create(Topic topic) {
    String topicName = topic.getName();
    String ownerGroupName = topic.getOwnerGroup().getName();
    kafkaClusterProxy.createKafkaCluster(topicName);
    TopicDatabaseInfo topicDatabaseInfo = TopicDatabaseInfo.builder().name(topicName).ownerGroup(topic.getOwnerGroup()).build();
//    TopicEntity topicEntity = TopicEntity.builder().topicName(topicName).ownerGroup(ownerGroupName).build();
    topicDaoDynamoDb.saveTopicInfo(topicDatabaseInfo);
  }

  @Override
  public Optional<Topic> get(String topicName) {
    boolean topicExistsInKafkaCluster = kafkaClusterProxy.topicExistsInKafkaCluster(topicName);
    Optional<TopicDatabaseInfo> topicDatabaseInfo = topicDaoDynamoDb.getTopicInfo(topicName);
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
    log.info("deleting topic: {}", topicName);
    kafkaClusterProxy.delete(topicName);
    topicDaoDynamoDb.deleteTopicInfo(topicName);
  }

}
