package com.example.topics.core;

import java.util.Optional;

public interface TopicRepository {
  void create(Topic topic);

  Optional<Topic> get(String topicName);

  void delete(String topicName);
}
