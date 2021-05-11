package com.example.topics.delete.lambdaauthorizer.infra;

import com.example.topics.BaseHandlerTest;
import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;

class DeleteAuthorizerHandlerTest extends BaseHandlerTest {

  private static final String API_LIVE_STAGE_ARN = "someApiLiveStageArn";
  protected static DeleteAuthorizerHandler deleteAuthorizerHandler;
  private String TOPIC_NAME = "testTopicName";
  private Group topicOwnerGroup;
  private Topic topic;
  private JsonNode requestTree;

  @SneakyThrows
  @BeforeEach
  void setUp() {
    setEnvVarIfNotDefined("API_LIVE_STAGE_ARN", API_LIVE_STAGE_ARN);
    deleteAuthorizerHandler = new DeleteAuthorizerHandler();
    topicOwnerGroup = new Group("dev");
    topic = Topic.builder().name(TOPIC_NAME).ownerGroup(topicOwnerGroup).build();
    mockTopicRepository();
    createTestRequest();
  }

  private void mockTopicRepository() {
    when(topicRepository.get(TOPIC_NAME)).thenReturn(Optional.of(topic));
  }

  private void createTestRequest() throws IOException {
    String requestString = FileUtils.readFileToString(new File("src/test/resources/deleteTopic.json"), StandardCharsets.UTF_8);
    requestTree = mapper.readTree(requestString);
    ((ObjectNode) requestTree.at("/pathParameters")).put("topic", TOPIC_NAME);
  }

  @Test
  void givenTopicExists_whenDeletionCalledByOwner_thenAuthorized() {
    // GIVEN
    Map<String, Object> request = mapper.convertValue(requestTree, new TypeReference<>() {
    });

    // WHEN
    JsonNode jsonNode = mapper.valueToTree(deleteAuthorizerHandler.handleRequest(request, testContext));

    // THEN
    String effect = jsonNode.at("/policyDocument/Statement/0/Effect").asText();
    Assertions.assertThat(effect).isEqualTo("Allow");
  }

  @Test
  void givenTopicBelongsToAnotherGroup_whenDeletionCalledByNotOwner_thenNotAuthorized() {
    // GIVEN
    Topic topic = Topic.builder().name(TOPIC_NAME).ownerGroup(new Group("anotherGroup")).build();
    when(topicRepository.get(TOPIC_NAME)).thenReturn(Optional.of(topic));
    Map<String, Object> request = mapper.convertValue(requestTree, new TypeReference<>() {
    });

    // WHEN
    JsonNode jsonNode = mapper.valueToTree(deleteAuthorizerHandler.handleRequest(request, testContext));

    // THEN
    String effect = jsonNode.at("/policyDocument/Statement/0/Effect").asText();
    Assertions.assertThat(effect).isEqualTo("Deny");
  }

  @Test
  /**
   * the call is authorized, the api will subsequently return http 404 ... until we know how to make the api return 404 from the lambda authorizer, if possible at all
   */
  void givenTopicDoesNotExist_whenDeletionCalled_thenAuthorized() {
    // GIVEN
    when(topicRepository.get(TOPIC_NAME)).thenReturn(Optional.empty());
    Map<String, Object> request = mapper.convertValue(requestTree, new TypeReference<>() {
    });

    // WHEN
    JsonNode jsonNode = mapper.valueToTree(deleteAuthorizerHandler.handleRequest(request, testContext));

    // THEN
    String effect = jsonNode.at("/policyDocument/Statement/0/Effect").asText();
    Assertions.assertThat(effect).isEqualTo("Allow");
  }
}