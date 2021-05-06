package com.example.topics.core;

import java.util.Optional;

public interface TopicDao {
  public void saveTopicInfo(Topic topic);

  Optional<Topic> getTopicInfo(String topicName);

  void deleteTopicInfo(Topic topic);
}
