package com.example.topics.details;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import com.example.topics.core.TopicDatabaseInfo;
import com.example.topics.core.TopicRepository;
import com.example.topics.infra.GatewayResponse;
import com.example.topics.infra.TopicRepositoryFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class GetTopicDetailsHandler implements RequestHandler<Map<String, Object>, GatewayResponse<Optional<Topic>>> {

  private final TopicRepository topicRepository;
  private final ObjectMapper mapper = new ObjectMapper();

  public GetTopicDetailsHandler() {
    topicRepository = TopicRepositoryFactory.getInstance().buildTopicRepositoryFactory();
  }

  @SneakyThrows
  @Override
  public GatewayResponse<Optional<Topic>> handleRequest(Map<String, Object> request, Context context) {
    JsonNode jsonNode = mapper.valueToTree(request);
    log.info("received event {}", jsonNode.toString());
    String topicName = jsonNode.at("/pathParameters/topic").textValue();
    return GatewayResponse.createResponse(topicRepository.get(topicName), 200);
  }

}
