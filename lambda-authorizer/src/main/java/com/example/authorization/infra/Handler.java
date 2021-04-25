package com.example.authorization.infra;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.authorization.core.AuthorizationDecider;
import com.example.authorization.core.Group;
import com.example.authorization.core.Topic;
import com.example.authorization.core.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class Handler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

  private ObjectMapper mapper = new ObjectMapper();
  private AuthorizationDecider authorizationDecider = new AuthorizationDecider();
  private JwtUserMapper jwtUserMapper = new JwtUserMapper();

  @SneakyThrows
  @Override
  public Map<String, Object> handleRequest(Map<String, Object> request, Context context) {
    log.info("request: {}", mapper.writeValueAsString(request));
    log.info("context: {}", context.toString());
    JsonNode requestJsonNode = mapper.valueToTree(request);
    String authorizationToken = getAuthorizationToken(requestJsonNode);
    log.info("authToken: {}", authorizationToken);

    String topicName = requestJsonNode.at("/pathParameters/topic").asText();
    log.info("topicName: {}", topicName);
    Topic topic = getMockTopic(topicName);
    User user = jwtUserMapper.getUserFromJwt(authorizationToken);
    log.info("group1: {}", new Group("group1"));
    log.info("topic: {}", topic);
    log.info("user: {}", user);

    boolean isAuthorized = authorizationDecider.isDeletionAuthorized(user, topic);
    String authorizationResponse = isAuthorized ? "Allow" : "Deny";

    String response = "{\"principalId\":\"abc123\",\"policyDocument\":{\"Version\":\"2012-10-17\",\"Statement\":[{\"Action\":\"execute-api:Invoke\",\"Resource\":[\"arn:aws:execute-api:eu-west-3:274314838444:orhscngdud/*/*\"],\"Effect\":\"" + authorizationResponse + "\"}]}}";
    return mapper.readValue(response, new TypeReference<HashMap<String, Object>>() {
    });
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
        .ownerGroup(new Group(groupName))
        .build();
  }

  private String getAuthorizationToken(JsonNode request) {
    return request.at("/headers/Authorization").asText();
  }
}
