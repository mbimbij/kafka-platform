package com.example.topics.create;

import com.example.topics.BaseLocalDockerIT;
import com.example.topics.core.Group;
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
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class CreateTopicHandlerLocalDockerIT extends BaseLocalDockerIT {

  @SneakyThrows
  @Test
  void givenTopicNotExisting_whenCreateTopic_thenTopicCreated() {
    // GIVEN
    CreateTopicHandler createTopicHandler = new CreateTopicHandler();
    String createTopicRequest = FileUtils.readFileToString(new File("src/test/resources/createTopic.json"), StandardCharsets.UTF_8);
    Map<String, Object> request = new ObjectMapper().readValue(createTopicRequest, new TypeReference<>() {
    });
    request.put("topicName", correlationId);
    CreateTopicResponse expectedCreateTopicResponse = new CreateTopicResponse(correlationId, new Group((String) request.get("ownerGroup")));

    // WHEN
    GatewayResponse<CreateTopicResponse> gatewayResponse = createTopicHandler.handleRequest(request, testContext);
    CreateTopicResponse actualCreateTopicResponse = mapper.readValue(gatewayResponse.getBody(), CreateTopicResponse.class);

    // THEN
    SoftAssertions.assertSoftly(softAssertions -> {
      assertThat(actualCreateTopicResponse).isEqualTo(expectedCreateTopicResponse);
      assertThat(topicDaoDynamoDb.getTopicInfo(correlationId)).hasValueSatisfying(topic -> Objects.equals(topic.getName(), correlationId));
      assertThat(kafkaClusterProxy.topicExistsInKafkaCluster(correlationId)).isTrue();
    });
  }
}