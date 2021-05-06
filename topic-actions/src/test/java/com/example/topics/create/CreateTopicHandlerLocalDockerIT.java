package com.example.topics.create;

import com.example.topics.BaseLocalDockerIT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.SneakyThrows;
import org.apache.kafka.clients.admin.TopicDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
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

    // WHEN
    createTopicHandler.handleRequest(request, testContext);

    // THEN - topic infos are created in dynamo
    assertThat(topicDaoDynamoDbImpl.getTopicInfo(correlationId)).hasValueSatisfying(topic -> Objects.equals(topic.getName(), correlationId));

    // THEN - topic is created in Kafka cluster
    Collection<TopicDescription> topicDescriptions = Try.of(() -> adminClient.describeTopics(Collections.singleton(correlationId)).all().get())
        .map(Map::values)
        .get();
    assertThat(topicDescriptions).hasSize(1);
    assertThat(topicDescriptions)
        .anyMatch(topicDescription -> Objects.equals(topicDescription.name(), correlationId));
  }
}