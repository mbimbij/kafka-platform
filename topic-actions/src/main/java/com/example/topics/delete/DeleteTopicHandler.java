package com.example.topics.delete;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.topics.core.TopicRepository;
import com.example.topics.create.CreateTopicCore;
import com.example.topics.create.CreateTopicRequest;
import com.example.topics.infra.GatewayResponse;
import com.example.topics.infra.JwtUserMapper;
import com.example.topics.infra.TopicRepositoryFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class DeleteTopicHandler implements RequestHandler<Map<String, Object>, GatewayResponse<Void>> {

  private final TopicRepository topicRepository;
  private ObjectMapper mapper = new ObjectMapper();

  public DeleteTopicHandler() {
    topicRepository = TopicRepositoryFactory.getInstance().buildTopicRepositoryFactory();
  }

  @SneakyThrows
  @Override
  public GatewayResponse<Void> handleRequest(Map<String, Object> request, Context context) {
    JsonNode jsonNode = mapper.valueToTree(request);
    log.info("received event {}", jsonNode.toString());
    String topicName = jsonNode.at("/pathParameters/topic").textValue();
    topicRepository.delete(topicName);
    return GatewayResponse.createHttp200Response(null);
  }
}
