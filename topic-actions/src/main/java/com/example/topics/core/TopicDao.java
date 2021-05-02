package com.example.topics.core;

import java.util.Optional;

public interface TopicDao {
  public void saveTopicInfo(Topic topic);

  Optional<Topic> getTopicInfo(Topic topic);

  void deleteTopicInfo(Topic topic);
}
