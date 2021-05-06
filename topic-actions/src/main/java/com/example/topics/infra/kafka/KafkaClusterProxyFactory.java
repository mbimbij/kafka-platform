package com.example.topics.infra.kafka;

import com.example.topics.infra.EnvironmentVariables;
import com.example.topics.infra.TopicRepositoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

import java.util.Optional;
import java.util.Properties;

@Slf4j
public class KafkaClusterProxyFactory {
  public static final long DEFAULT_TOPIC_CREATION_TIMEOUT_MILLIS = 5000;

  public static AdminClient createAdminClient() {
    Properties config = new Properties();
    String bootstrapServers = EnvironmentVariables.instance().get("BOOTSTRAP_SERVERS");
    log.info("bootstrapServers: {}", bootstrapServers);
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    return AdminClient.create(config);
  }

  public static KafkaClusterProxy buildKafkaClusterProxy() {
    long topicCreationTimeoutMillis = Optional.ofNullable(EnvironmentVariables.instance().get("TOPIC_CREATION_TIMEOUT_MILLIS"))
        .map(Long::parseLong)
        .orElse(DEFAULT_TOPIC_CREATION_TIMEOUT_MILLIS);
    return new KafkaClusterProxy(createAdminClient(), topicCreationTimeoutMillis);
  }
}
