package com.example.topics.delete;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.topics.core.TopicRepository;
import com.example.topics.create.CreateTopicCore;
import com.example.topics.create.CreateTopicRequest;
import com.example.topics.infra.JwtUserMapper;
import com.example.topics.infra.TopicRepositoryFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class DeleteTopicHandler implements RequestHandler<Map<String, Object>, Void> {

  private JwtUserMapper jwtUserMapper = new JwtUserMapper();
  private ObjectMapper mapper = new ObjectMapper();
  private final TopicRepository topicRepository;

  public DeleteTopicHandler() {
    topicRepository = TopicRepositoryFactory.buildTopicRepositoryFactory();
  }

  @SneakyThrows
  @Override
  public Void handleRequest(Map<String, Object> request, Context context) {
    String topicName = (String) request.get("topicName");
    topicRepository.delete(topicName);
    return null;
  }
}
