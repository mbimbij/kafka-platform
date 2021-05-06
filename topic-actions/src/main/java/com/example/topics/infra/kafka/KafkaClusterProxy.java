package com.example.topics.infra.kafka;

import com.example.topics.infra.TopicRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class KafkaClusterProxy {
  private final AdminClient admin;
  private final long topicCreationTimeoutMillis;
  private static final long defaultTopicCreationTimeoutMillis = 5000;

  @SneakyThrows
  public void createKafkaCluster(String topicName, TopicRepositoryImpl topicRepository) {
    NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
    Set<NewTopic> newTopics = Collections.singleton(newTopic);
    admin.createTopics(newTopics).all().get(topicCreationTimeoutMillis, TimeUnit.MILLISECONDS);
  }

  @SneakyThrows
  public boolean topicExistsInKafkaCluster(TopicRepositoryImpl topicRepository) {
    return !admin.listTopics().names().get().isEmpty();
  }
}
