package com.example.topics.details;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

@Slf4j
public class GetTopicDetailsHandler implements RequestHandler<Map<String, Object>, Topic> {

  @SneakyThrows
  @Override
  public Topic handleRequest(Map<String, Object> request, Context context) {
    String topicName = (String) request.get("topicName");
    return getMockTopic(topicName);
  }

  private Topic getMockTopic(String topicName) {
    String groupName;

    if (Objects.equals(topicName, "topic1")) {
      groupName = "group1";
    } else if (Objects.equals(topicName, "topic2")) {
      groupName = "group2";
    } else {
      groupName = "unknown";
    }

    return Topic.builder()
        .name(topicName)
        .ownerGroup(new Group(groupName))
        .build();
  }
}
