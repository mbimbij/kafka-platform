package com.example.topics.details;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import com.example.topics.core.TopicDatabaseInfo;
import com.example.topics.core.TopicRepository;
import com.example.topics.infra.GatewayResponse;
import com.example.topics.infra.TopicRepositoryFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class GetTopicDetailsHandler implements RequestHandler<Map<String, Object>, GatewayResponse<Optional<Topic>>> {

  private final TopicRepository topicRepository;

  public GetTopicDetailsHandler() {
    topicRepository = TopicRepositoryFactory.buildTopicRepositoryFactory();
  }

  @SneakyThrows
  @Override
  public GatewayResponse<Optional<Topic>> handleRequest(Map<String, Object> request, Context context) {
    String topicName = (String) request.get("topicName");
    return GatewayResponse.createResponse(topicRepository.get(topicName), 200);
  }

}
