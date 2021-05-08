package com.example.topics.create;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import com.example.topics.core.User;
import com.example.topics.infra.GatewayResponse;
import com.example.topics.infra.JwtUserMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class CreateTopicHandler implements RequestHandler<Map<String, Object>, GatewayResponse<CreateTopicResponse>> {

  private JwtUserMapper jwtUserMapper = new JwtUserMapper();
  private ObjectMapper mapper = new ObjectMapper();
  private final CreateTopicCore handlerCore;

  public CreateTopicHandler() {
    handlerCore = CreateTopicCore.createInstance();
  }

  @SneakyThrows
  @Override
  public GatewayResponse<CreateTopicResponse> handleRequest(Map<String, Object> request, Context context) {
    JsonNode bodyTree = mapper.readTree((String) request.get("body"));
    User user = jwtUserMapper.getUserFromJwt(getAuthorizationToken(mapper.valueToTree(request)));
    Group ownerGroup = new Group(bodyTree.get("ownerGroup").asText());
    String topicName = bodyTree.get("topicName").asText();
    Topic topicDatabaseInfo = Topic.builder().name(topicName).ownerGroup(ownerGroup).build();
    CreateTopicRequest createTopicRequest = new CreateTopicRequest(topicDatabaseInfo, user);
    CreateTopicResponse createTopicResponse = handlerCore.createTopic(createTopicRequest);
    return GatewayResponse.createResponse(createTopicResponse, 201);
  }

  private String getAuthorizationToken(JsonNode request) {
    return request.at("/headers/Authorization").asText();
  }
}
