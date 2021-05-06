package com.example.topics.core;

import java.util.Optional;

public interface KafkaProxy {
  void createTopic(String topicName);
  Optional<TopicClusterInfo> getTopicClusterInfo(String topicName);
}
