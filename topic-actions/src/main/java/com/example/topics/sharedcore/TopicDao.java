package com.example.topics.sharedcore;

import java.util.Optional;

public interface TopicDao {
  public void saveTopicInfo(Topic topic);

  Optional<Topic> getTopicInfo(Topic topic);

  Optional<Topic> getTopicInfo(String topicName);

  void deleteTopicInfo(Topic topic);
}
