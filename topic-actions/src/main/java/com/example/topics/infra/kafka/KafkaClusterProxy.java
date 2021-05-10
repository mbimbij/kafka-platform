package com.example.topics.infra.kafka;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class KafkaClusterProxy {
  private final AdminClient admin;
  private final long topicCreationTimeoutMillis;
  private static final long defaultTopicCreationTimeoutMillis = 5000;

  @SneakyThrows
  public void createKafkaCluster(String topicName) {
    NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
    Set<NewTopic> newTopics = Collections.singleton(newTopic);
    admin.createTopics(newTopics).all().get(topicCreationTimeoutMillis, TimeUnit.MILLISECONDS);
  }

  @SneakyThrows
  public boolean topicExistsInKafkaCluster(String topicName) {
    return admin.listTopics().names()
        .get().stream()
        .anyMatch(topicName::equals);
  }

  public void delete(String topicName) {
    log.info("deleting topic: {}", topicName);
    admin.deleteTopics(Collections.singleton(topicName));
  }
}
