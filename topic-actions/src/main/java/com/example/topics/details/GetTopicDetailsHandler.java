package com.example.topics.details;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import com.example.topics.core.TopicDatabaseInfo;
import com.example.topics.core.TopicRepository;
import com.example.topics.infra.TopicRepositoryFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class GetTopicDetailsHandler implements RequestHandler<Map<String, Object>, Optional<Topic>> {

  private final TopicRepository topicRepository;

  public GetTopicDetailsHandler() {
    topicRepository = TopicRepositoryFactory.buildTopicRepositoryFactory();
  }

  @SneakyThrows
  @Override
  public Optional<Topic> handleRequest(Map<String, Object> request, Context context) {
    String topicName = (String) request.get("topicName");
    return topicRepository.get(topicName);
  }

  private TopicDatabaseInfo getMockTopic(String topicName) {
    String groupName;

    if (Objects.equals(topicName, "topic1")) {
      groupName = "group1";
    } else if (Objects.equals(topicName, "topic2")) {
      groupName = "group2";
    } else {
      groupName = "unknown";
    }

    return TopicDatabaseInfo.builder()
        .name(topicName)
        .ownerGroup(new Group(groupName))
        .build();
  }
}
