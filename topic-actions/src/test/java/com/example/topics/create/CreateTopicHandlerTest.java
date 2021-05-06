package com.example.topics.create;

import com.amazonaws.services.lambda.runtime.Context;
import com.example.topics.TestContext;
import com.example.topics.core.KafkaProxy;
import com.example.topics.core.TopicDatabaseInfo;
import com.example.topics.core.TopicDao;
import com.example.topics.infra.dao.TopicDaoFactory;
import com.example.topics.infra.kafka.KafkaProxyFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.mockito.Mockito.*;

class CreateTopicHandlerTest {

  private static final String TEST_TOPIC_NAME = "testTopic";

  @SneakyThrows
  @Test
  void whenUserSendsCreateTopicRequest_thenTopicCreated_andInfoSavedToDatabase() {
    // GIVEN
    TopicDao topicDao = mock(TopicDao.class);
    KafkaProxy kafkaProxy = mock(KafkaProxy.class);
    CreateTopicHandler createTopicHandler;
    try (
        MockedStatic<TopicDaoFactory> topicDaoFactoryMockedStatic = mockStatic(TopicDaoFactory.class);
        MockedStatic<KafkaProxyFactory> kafkaProxyFactoryMockedStatic = mockStatic(KafkaProxyFactory.class)
    ) {
      topicDaoFactoryMockedStatic.when(TopicDaoFactory::buildTopicDao).thenReturn(topicDao);
      kafkaProxyFactoryMockedStatic.when(KafkaProxyFactory::createKafkaProxy).thenReturn(kafkaProxy);
      createTopicHandler = new CreateTopicHandler();
    }

    String createTopicRequest = FileUtils.readFileToString(new File("src/test/resources/createTopic.json"), StandardCharsets.UTF_8);
    Map<String, Object> request = new ObjectMapper().readValue(createTopicRequest, new TypeReference<>() {
    });
    request.put("topicName", TEST_TOPIC_NAME);
    Context testContext = TestContext.builder().build();

    // WHEN
    createTopicHandler.handleRequest(request, testContext);

    // THEN
    verify(topicDao).saveTopicInfo(any(TopicDatabaseInfo.class));
    verify(kafkaProxy).createTopic(TEST_TOPIC_NAME);
  }

  @SneakyThrows
  @Test
  void whenUserDoesNotBelongToGroupRequested_thenIllegalArgumentException_andTopicNotCreated_andTopicInfoNotPersistedToDatabase() {
    // GIVEN
    TopicDao topicDao = mock(TopicDao.class);
    KafkaProxy kafkaProxy = mock(KafkaProxy.class);
    CreateTopicHandler createTopicHandler;
    try (
        MockedStatic<TopicDaoFactory> topicDaoFactoryMockedStatic = mockStatic(TopicDaoFactory.class);
        MockedStatic<KafkaProxyFactory> kafkaProxyFactoryMockedStatic = mockStatic(KafkaProxyFactory.class)
    ) {
      topicDaoFactoryMockedStatic.when(TopicDaoFactory::buildTopicDao).thenReturn(topicDao);
      kafkaProxyFactoryMockedStatic.when(KafkaProxyFactory::createKafkaProxy).thenReturn(kafkaProxy);
      createTopicHandler = new CreateTopicHandler();
    }

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
    verify(topicDao, never()).saveTopicInfo(any(TopicDatabaseInfo.class));
    verify(kafkaProxy, never()).createTopic(anyString());
  }
}