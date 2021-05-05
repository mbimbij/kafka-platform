package com.example.topics.create;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.example.topics.infra.AdminClientFactory;
import com.example.topics.infra.EnvironmentVariables;
import com.example.topics.sharedcore.Group;
import com.example.topics.sharedcore.Topic;
import com.example.topics.sharedcore.TopicDao;
import com.example.topics.sharedcore.User;
import com.example.topics.infra.dao.TopicDaoFactory;
import com.example.topics.infra.JwtUserMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CreateTopicHandler implements RequestHandler<Map<String, Object>, Void> {

  private final AdminClient admin;
  private final long topicCreationTimeoutMillis;
  private static final long defaultTopicCreationTimeoutMillis = 5000;
  private final TopicDao topicDao;
  private JwtUserMapper jwtUserMapper = new JwtUserMapper();
  private ObjectMapper mapper = new ObjectMapper();

  public CreateTopicHandler() {
    topicCreationTimeoutMillis = Optional.ofNullable(EnvironmentVariables.instance().get("TOPIC_CREATION_TIMEOUT_MILLIS"))
        .map(Long::parseLong)
        .orElse(defaultTopicCreationTimeoutMillis);
    log.info("topicCreationTimeoutMillis: {}", topicCreationTimeoutMillis);
    admin = AdminClientFactory.createAdminClient();
    topicDao = TopicDaoFactory.buildTopicDao();
  }

  @SneakyThrows
  @Override
  public Void handleRequest(Map<String, Object> request, Context context) {
    JsonNode requestJsonNode = mapper.valueToTree(request);
    User user = jwtUserMapper.getUserFromJwt(getAuthorizationToken(requestJsonNode));
    Group ownerGroup = new Group((String) request.get("ownerGroup"));
    validateUserBelongsToSpecifiedOwnerGroup(user, ownerGroup);

    String topicName = (String) request.get("topicName");
    NewTopic newTopic = new NewTopic(topicName, 1, (short) 1);
    Set<NewTopic> newTopics = Collections.singleton(newTopic);
    admin.createTopics(newTopics).all().get(topicCreationTimeoutMillis, TimeUnit.MILLISECONDS);

    topicDao.saveTopicInfo(Topic.builder().name(topicName).ownerGroup(ownerGroup).build());
    log.info("topicName: {}", topicName);
    return null;
  }

  private void validateUserBelongsToSpecifiedOwnerGroup(User user, Group ownerGroup) {
    if(!user.getGroups().contains(ownerGroup)){
      throw new IllegalArgumentException("user \""+ user.getName()+"\" does not belong to the group \""+ ownerGroup
          +"\" and as such cannot create a topic with that group as its owner");
    }
  }

  private String getAuthorizationToken(JsonNode request) {
    return request.at("/headers/Authorization").asText();
  }
}
