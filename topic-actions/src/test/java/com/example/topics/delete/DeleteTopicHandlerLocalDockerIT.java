package com.example.topics.delete;

import com.example.topics.BaseLocalDockerIT;
import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DeleteTopicHandlerLocalDockerIT extends BaseLocalDockerIT {
  @SneakyThrows
  @Test
  void givenTopicExistsInKafkaCluster_andTopicExistsInDatabase_whenDelete_thenTopicDeletedFromKafkaCluster_andDeletedFromDatabase() {
    // GIVEN
    DeleteTopicHandler deleteTopicHandler = new DeleteTopicHandler();
    Topic topic = Topic.builder().name(correlationId).ownerGroup(new Group("group")).build();
    topicRepository.create(topic);
    SoftAssertions.assertSoftly(softAssertions -> {
      softAssertions.assertThat(kafkaClusterProxy.topicExistsInKafkaCluster(correlationId)).isTrue();
      softAssertions.assertThat(topicDaoDynamoDb.getTopicInfo(correlationId)).isNotEmpty();
    });
    String requestString = FileUtils.readFileToString(new File("src/test/resources/deleteTopic.json"), StandardCharsets.UTF_8);
    JsonNode requestTree = mapper.readTree(requestString);
    ((ObjectNode) requestTree.at("/pathParameters")).put("topic", correlationId);
    Map<String, Object> request = mapper.convertValue(requestTree, new TypeReference<>() {
    });

    // WHEN
    deleteTopicHandler.handleRequest(request, testContext);

    // THEN
    SoftAssertions.assertSoftly(softAssertions -> {
      softAssertions.assertThat(kafkaClusterProxy.topicExistsInKafkaCluster(correlationId)).isFalse();
      softAssertions.assertThat(topicDaoDynamoDb.getTopicInfo(correlationId)).isEmpty();
    });
  }
}
