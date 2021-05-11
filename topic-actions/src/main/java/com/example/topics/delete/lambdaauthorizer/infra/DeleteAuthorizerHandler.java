package com.example.topics.delete.lambdaauthorizer.infra;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.topics.core.TopicRepository;
import com.example.topics.core.User;
import com.example.topics.delete.lambdaauthorizer.core.AuthorizationDecider;
import com.example.topics.infra.EnvironmentVariables;
import com.example.topics.infra.TopicRepositoryFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DeleteAuthorizerHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

  private ObjectMapper mapper = new ObjectMapper();
  private JwtUserMapper jwtUserMapper;
  private AuthorizationDecider authorizationDecider;
  private TopicRepository topicRepository;

  public DeleteAuthorizerHandler() {
    topicRepository = TopicRepositoryFactory.getInstance().buildTopicRepositoryFactory();
    authorizationDecider = new AuthorizationDecider();
    jwtUserMapper = new JwtUserMapper();
  }

  @SneakyThrows
  @Override
  public Map<String, Object> handleRequest(Map<String, Object> request, Context context) {
    log.info(mapper.writeValueAsString(request));
    JsonNode requestJsonNode = mapper.valueToTree(request);
    String authorizationToken = getAuthorizationToken(requestJsonNode);

    String topicName = requestJsonNode.at("/pathParameters/topic").asText();
    User user = jwtUserMapper.getUserFromJwt(authorizationToken);

    // if topic does not exist, the deletion method still can be called, but will return http404
    boolean isAuthorized = topicRepository.get(topicName)
        .map(topic -> authorizationDecider.isDeletionAuthorized(user, topic))
        .orElse(true);
    String authorizationResponse = isAuthorized ? "Allow" : "Deny";

    String methodArn = requestJsonNode.get("methodArn").asText();
    String response = "{\"principalId\":\"abc123\",\"policyDocument\":{\"Version\":\"2012-10-17\",\"Statement\":[{\"Action\":\"execute-api:Invoke\",\"Resource\":[\"" + methodArn + "\"],\"Effect\":\"" + authorizationResponse + "\"}]}}";
    return mapper.readValue(response, new TypeReference<HashMap<String, Object>>() {
    });
  }

  private String getAuthorizationToken(JsonNode request) {
    return request.at("/headers/Authorization").asText();
  }
}
