package com.example.topics.details;

import com.example.topics.BaseLocalDockerIT;
import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class GetTopicDetailsHandlerLocalDockerIT extends BaseLocalDockerIT {
  @SneakyThrows
  @Test
  void givenTopicExistsInClusterAndDatabase_thenReturnsTopicInfoFromDatabase() {
    // GIVEN
    GetTopicDetailsHandler getTopicDetailsHandler = new GetTopicDetailsHandler();

    Group ownerGroup = new Group("group1");
    Topic topic = new Topic(correlationId, ownerGroup);
    topicRepository.create(topic);

    String createTopicRequest = FileUtils.readFileToString(new File("src/test/resources/getTopicDetails.json"), StandardCharsets.UTF_8);
    Map<String, Object> request = new ObjectMapper().readValue(createTopicRequest, new TypeReference<>() {
    });
    request.put("topicName", correlationId);

    // WHEN
    Optional<Topic> actualTopicDatabaseInfo = getTopicDetailsHandler.handleRequest(request, testContext);

    // THEN
    assertThat(actualTopicDatabaseInfo).hasValue(topic);
  }
}