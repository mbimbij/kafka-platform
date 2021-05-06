package com.example.topics.create;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.topics.core.*;
import com.example.topics.infra.dao.TopicDaoFactory;
import com.example.topics.infra.JwtUserMapper;
import com.example.topics.infra.kafka.KafkaProxyFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class CreateTopicHandler implements RequestHandler<Map<String, Object>, Void> {

  private final KafkaProxy kafkaProxy;
  private final TopicDao topicDao;
  private JwtUserMapper jwtUserMapper = new JwtUserMapper();
  private ObjectMapper mapper = new ObjectMapper();
  public static final String USER_NOT_IN_GROUP_ERROR_MESSAGE_EXCERPT = "does not belong to the group";

  public CreateTopicHandler() {
    topicDao = TopicDaoFactory.buildTopicDao();
    kafkaProxy = KafkaProxyFactory.createKafkaProxy();
  }

  @SneakyThrows
  @Override
  public Void handleRequest(Map<String, Object> request, Context context) {
    JsonNode requestJsonNode = mapper.valueToTree(request);
    User user = jwtUserMapper.getUserFromJwt(getAuthorizationToken(requestJsonNode));
    Group ownerGroup = new Group((String) request.get("ownerGroup"));
    validateUserBelongsToSpecifiedOwnerGroup(user, ownerGroup);

    String topicName = (String) request.get("topicName");
    kafkaProxy.createTopic(topicName);
    topicDao.saveTopicInfo(Topic.builder().name(topicName).ownerGroup(ownerGroup).build());
    log.info("topicName: {}", topicName);
    return null;
  }

  private void validateUserBelongsToSpecifiedOwnerGroup(User user, Group ownerGroup) {
    if(!user.getGroups().contains(ownerGroup)){
      throw new IllegalArgumentException("user \""+ user.getName()+ "\" " + USER_NOT_IN_GROUP_ERROR_MESSAGE_EXCERPT + " \"" + ownerGroup
          +"\" and as such cannot create a topic with that group as its owner");
    }
  }

  private String getAuthorizationToken(JsonNode request) {
    return request.at("/headers/Authorization").asText();
  }
}
