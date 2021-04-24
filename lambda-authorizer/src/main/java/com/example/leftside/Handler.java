package com.example.leftside;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class Handler implements RequestHandler<Map<String,Object>, Map<String,Object>> {

  private ObjectMapper mapper = new ObjectMapper();

  @SneakyThrows
  @Override
  public Map<String,Object> handleRequest(Map<String,Object> request, Context context) {
    log.info("request: {}", mapper.writeValueAsString(request));
    String authorizationToken = (String) request.get("authorizationToken");
    log.info("authToken: {}", authorizationToken);

    boolean isAuthorized = Objects.equals(authorizationToken, "abc123");
    String authorizationResponse = isAuthorized ? "Allow": "Deny";

    String response = "{\"principalId\":\"abc123\",\"policyDocument\":{\"Version\":\"2012-10-17\",\"Statement\":[{\"Action\":\"execute-api:Invoke\",\"Resource\":[\"arn:aws:execute-api:eu-west-3:274314838444:orhscngdud/*/*\"],\"Effect\":\"" + authorizationResponse + "\"}]}}";
    return mapper.readValue(response, new TypeReference<HashMap<String,Object>>(){});
  }
}
