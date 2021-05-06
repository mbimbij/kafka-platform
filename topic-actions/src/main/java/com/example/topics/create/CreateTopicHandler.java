package com.example.topics.create;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import com.example.topics.core.User;
import com.example.topics.infra.JwtUserMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class CreateTopicHandler implements RequestHandler<Map<String, Object>, Void> {

  private JwtUserMapper jwtUserMapper = new JwtUserMapper();
  private ObjectMapper mapper = new ObjectMapper();
  private final CreateTopicCore handlerCore;

  public CreateTopicHandler() {
    handlerCore = CreateTopicCore.createInstance();
  }

  @SneakyThrows
  @Override
  public Void handleRequest(Map<String, Object> request, Context context) {
    JsonNode requestJsonNode = mapper.valueToTree(request);
    User user = jwtUserMapper.getUserFromJwt(getAuthorizationToken(requestJsonNode));
    Group ownerGroup = new Group((String) request.get("ownerGroup"));
    String topicName = (String) request.get("topicName");
    Topic topic = Topic.builder().name(topicName).ownerGroup(ownerGroup).build();
    CreateTopicRequest createTopicRequest = new CreateTopicRequest(topic, user);
    handlerCore.createTopic(createTopicRequest);
    return null;
  }

  private String getAuthorizationToken(JsonNode request) {
    return request.at("/headers/Authorization").asText();
  }
}
