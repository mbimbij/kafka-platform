package com.example.topics.core;

import java.util.Optional;

public interface TopicDao {
  public void saveTopicInfo(TopicDatabaseInfo topicDatabaseInfo);

  Optional<TopicDatabaseInfo> getTopicInfo(String topicName);

  void deleteTopicInfo(TopicDatabaseInfo topicDatabaseInfo);
}
