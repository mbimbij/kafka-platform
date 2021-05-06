package com.example.topics.infra.kafka;

import com.example.topics.core.KafkaProxy;
import com.example.topics.infra.EnvironmentVariables;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KafkaProxyImpl implements KafkaProxy {
  private final AdminClient admin;
  private final long topicCreationTimeoutMillis;
  private static final long defaultTopicCreationTimeoutMillis = 5000;

  public KafkaProxyImpl() {
    topicCreationTimeoutMillis = Optional.ofNullable(EnvironmentVariables.instance().get("TOPIC_CREATION_TIMEOUT_MILLIS"))
        .map(Long::parseLong)
        .orElse(defaultTopicCreationTimeoutMillis);
    log.info("topicCreationTimeoutMillis: {}", topicCreationTimeoutMillis);
    admin = AdminClientFactory.createAdminClient();
  }

  @SneakyThrows
  @Override
  public void createTopic(String topicName) {
    NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
    Set<NewTopic> newTopics = Collections.singleton(newTopic);
    admin.createTopics(newTopics).all().get(topicCreationTimeoutMillis, TimeUnit.MILLISECONDS);
  }
}
