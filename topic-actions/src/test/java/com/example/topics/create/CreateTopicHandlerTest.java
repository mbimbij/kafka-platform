package com.example.topics.create;

import com.amazonaws.services.lambda.runtime.Context;
import com.example.topics.TestContext;
import com.example.topics.core.Topic;
import com.example.topics.core.TopicRepository;
import com.example.topics.infra.TopicRepositoryFactory;
import com.example.topics.infra.TopicRepositoryImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.mockito.Mockito.*;

class CreateTopicHandlerTest {

  private static final String TEST_TOPIC_NAME = "testTopic";
  private TopicRepository topicRepository;
  private CreateTopicHandler createTopicHandler;

  @BeforeEach
  void setUp() {
    topicRepository = mock(TopicRepositoryImpl.class);
    try (MockedStatic<TopicRepositoryFactory> topicRepositoryMockedStatic = mockStatic(TopicRepositoryFactory.class)) {
      topicRepositoryMockedStatic.when(TopicRepositoryFactory::buildTopicRepositoryFactory).thenReturn(topicRepository);
      createTopicHandler = new CreateTopicHandler();
    }
  }

  @SneakyThrows
  @Test
  void whenUserSendsCreateTopicRequest_thenTopicCreated_andInfoSavedToDatabase() {
    // GIVEN
    String createTopicRequest = FileUtils.readFileToString(new File("src/test/resources/createTopic.json"), StandardCharsets.UTF_8);
    Map<String, Object> request = new ObjectMapper().readValue(createTopicRequest, new TypeReference<>() {
    });
    request.put("topicName", TEST_TOPIC_NAME);
    Context testContext = TestContext.builder().build();

    // WHEN
    createTopicHandler.handleRequest(request, testContext);

    // THEN
    verify(topicRepository).create(any(Topic.class));
  }

  @SneakyThrows
  @Test
  void whenUserDoesNotBelongToGroupRequested_thenIllegalArgumentException_andTopicNotCreated_andTopicInfoNotPersistedToDatabase() {
    // GIVEN
    String createTopicRequest = FileUtils.readFileToString(new File("src/test/resources/createTopic.json"), StandardCharsets.UTF_8);
    Map<String, Object> request = new ObjectMapper().readValue(createTopicRequest, new TypeReference<>() {
    });
    request.put("topicName", TEST_TOPIC_NAME);
    request.put("ownerGroup", "anotherGroup");
    Context testContext = TestContext.builder().build();

    // WHEN
    Assertions.assertThatThrownBy(() -> createTopicHandler.handleRequest(request, testContext))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(CreateTopicCore.USER_NOT_IN_GROUP_ERROR_MESSAGE_EXCERPT);

    // THEN
    verify(topicRepository, never()).create(any(Topic.class));
  }
}