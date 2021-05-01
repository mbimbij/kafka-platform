package com.example.topics.create;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CreateTopicHandler implements RequestHandler<Map<String, Object>, Void> {


  private final Properties config;
  private final AdminClient admin;
  private final long topicCreationTimeoutMillis;

  public CreateTopicHandler() {
    config = new Properties();
    String bootstrapServers = System.getenv("BOOTSTRAP_SERVERS");
    topicCreationTimeoutMillis = Long.parseLong(System.getenv("TOPIC_CREATION_TIMEOUT_MILLIS"));
    log.info("bootstrapServers: {}", bootstrapServers);
    log.info("topicCreationTimeoutMillis: {}", topicCreationTimeoutMillis);
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    admin = AdminClient.create(config);
  }

  @SneakyThrows
  @Override
  public Void handleRequest(Map<String, Object> request, Context context) {
    String topicName = (String) request.get("topicName");
    NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
    Set<NewTopic> newTopics = Collections.singleton(newTopic);
    admin.createTopics(newTopics).all().get(topicCreationTimeoutMillis, TimeUnit.MILLISECONDS);
    log.info("topicName: {}", topicName);
    return null;
  }

}
