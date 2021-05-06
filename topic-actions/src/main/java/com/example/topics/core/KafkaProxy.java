package com.example.topics.core;

public interface KafkaProxy {
  void createTopic(String topicName) throws InterruptedException, java.util.concurrent.ExecutionException, java.util.concurrent.TimeoutException;
}
