package com.example.topics.create;

import com.example.topics.core.TopicDatabaseInfo;
import com.example.topics.core.User;
import lombok.Value;

@Value
public class CreateTopicRequest {
  TopicDatabaseInfo topicDatabaseInfo;
  User user;
}
