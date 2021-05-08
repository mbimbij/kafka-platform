package com.example.topics.details;

import com.example.topics.TestContext;
import com.example.topics.core.Topic;
import com.example.topics.core.TopicRepository;
import com.example.topics.infra.GatewayResponse;
import com.example.topics.infra.TopicRepositoryFactory;
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
import java.util.Optional;

import static org.mockito.Mockito.*;

class GetTopicDetailsHandlerTest {

  private TopicRepository topicRepository;
  private GetTopicDetailsHandler getTopicDetailsHandler;

  @BeforeEach
  void setUp() {
    topicRepository = mock(TopicRepository.class);
    try (MockedStatic<TopicRepositoryFactory> mocked = mockStatic(TopicRepositoryFactory.class)) {
      mocked.when(TopicRepositoryFactory::buildTopicRepositoryFactory).thenReturn(topicRepository);
      getTopicDetailsHandler = new GetTopicDetailsHandler();
    }
  }

  @SneakyThrows
  @Test
  void givenNoTopic_thenResponseIsEmptyJson_andNotNullString() {
    // GIVEN
    when(topicRepository.get(anyString())).thenReturn(Optional.empty());
    String createTopicRequest = FileUtils.readFileToString(new File("src/test/resources/getTopicDetails.json"), StandardCharsets.UTF_8);
    Map<String, Object> request = new ObjectMapper().readValue(createTopicRequest, new TypeReference<>() {
    });

    // WHEN
    GatewayResponse<Optional<Topic>> gatewayResponse = getTopicDetailsHandler.handleRequest(request, TestContext.builder().build());

    // THEN
    Assertions.assertThat(gatewayResponse.getBody()).isEqualTo("{}");
  }
}