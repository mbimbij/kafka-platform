package com.example.topics.details;

import com.example.topics.BaseLocalDockerIT;
import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import com.example.topics.infra.GatewayResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GetTopicDetailsHandlerLocalDockerIT extends BaseLocalDockerIT {

  private final GetTopicDetailsHandler getTopicDetailsHandler = new GetTopicDetailsHandler();

  @SneakyThrows
  @Test
  void givenTopicExistsInClusterAndDatabase_thenReturnsTopicInfoFromDatabase() {
    // GIVEN
    Group ownerGroup = new Group("group1");
    Topic topic = new Topic(correlationId, ownerGroup);
    topicRepository.create(topic);

    Map<String, Object> request = Map.of("topicName", correlationId);

    // WHEN
    GatewayResponse<Optional<Topic>> gatewayResponse = getTopicDetailsHandler.handleRequest(request, testContext);
    Optional<Topic> actualTopicDatabaseInfo = mapper.readValue(gatewayResponse.getBody(), new TypeReference<>() {
    });

    // THEN
    assertThat(actualTopicDatabaseInfo).hasValue(topic);
  }

  @SneakyThrows
  @Test
  void givenTopicDoesNotExist_thenEmptyJsonResponse() {
    // GIVEN
    Map<String, Object> request = Map.of("topicName", correlationId);

    // WHEN
    GatewayResponse<Optional<Topic>> gatewayResponse = getTopicDetailsHandler.handleRequest(request, testContext);
    String gatewayResponseBody = gatewayResponse.getBody();
    Optional<Topic> actualTopicDatabaseInfo = mapper.readValue(gatewayResponseBody, new TypeReference<>() {
    });

    // THEN
    SoftAssertions.assertSoftly(softAssertions -> {
      assertThat(gatewayResponseBody).isEqualTo("null");
      assertThat(actualTopicDatabaseInfo).isEmpty();
    });
  }
}