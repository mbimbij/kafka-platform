package com.example.topics.create;

import com.example.topics.core.Topic;
import com.example.topics.core.User;
import lombok.Value;

@Value
public class CreateTopicRequest {
  Topic topic;
  User user;
}
