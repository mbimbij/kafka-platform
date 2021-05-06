package com.example.authorization.infra;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.authorization.core.AuthorizationDecider;
import com.example.authorization.core.Topic;
import com.example.authorization.core.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.ec2.Ec2Client;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Handler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

  private ObjectMapper mapper = new ObjectMapper();
  private JwtUserMapper jwtUserMapper = new JwtUserMapper();
  private AuthorizationDecider authorizationDecider = new AuthorizationDecider();
  private TopicDetailsGateway topicDetailsGateway = new TopicDetailsGateway();
  private Regions currentRegion = Regions.fromName(System.getenv("AWS_REGION"));
  private String accountId = System.getenv("ACCOUNT_ID");

  @SneakyThrows
  @Override
  public Map<String, Object> handleRequest(Map<String, Object> request, Context context) {
    log.info("current region: {}", currentRegion.toString());
    log.info("request: {}", mapper.writeValueAsString(request));
    log.info("context: {}", context.toString());
    JsonNode requestJsonNode = mapper.valueToTree(request);
    String authorizationToken = getAuthorizationToken(requestJsonNode);
    log.info("authToken: {}", authorizationToken);

    String topicName = requestJsonNode.at("/pathParameters/topic").asText();
    log.info("topicName: {}", topicName);
    Topic topic = topicDetailsGateway.getTopicDetails(topicName);
    User user = jwtUserMapper.getUserFromJwt(authorizationToken);
    log.info("topic: {}", topic);
    log.info("user: {}", user);

    boolean isAuthorized = authorizationDecider.isDeletionAuthorized(user, topic);
    String authorizationResponse = isAuthorized ? "Allow" : "Deny";

    String apiGatewayStageArn = "arn:aws:execute-api:" + currentRegion.toString() + ":" + accountId + ":orhscngdud/*/*";
    String response = "{\"principalId\":\"abc123\",\"policyDocument\":{\"Version\":\"2012-10-17\",\"Statement\":[{\"Action\":\"execute-api:Invoke\",\"Resource\":[\"" + apiGatewayStageArn + "\"],\"Effect\":\"" + authorizationResponse + "\"}]}}";
    return mapper.readValue(response, new TypeReference<HashMap<String, Object>>() {
    });
  }

  private String getAuthorizationToken(JsonNode request) {
    return request.at("/headers/Authorization").asText();
  }
}
