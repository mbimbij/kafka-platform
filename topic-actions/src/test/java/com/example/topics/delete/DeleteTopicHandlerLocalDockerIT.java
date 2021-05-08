package com.example.topics.delete;

import com.example.topics.BaseLocalDockerIT;
import com.example.topics.core.Group;
import com.example.topics.core.Topic;
import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

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
    Map<String, String> body = new HashMap<>();
    body.put("topicName", correlationId);
    body.put("ownerGroup", "anotherGroup");
    Map<String, Object> request = Map.of("body", mapper.writeValueAsString(body));

    // WHEN
    deleteTopicHandler.handleRequest(request, testContext);

    // THEN
    SoftAssertions.assertSoftly(softAssertions -> {
      softAssertions.assertThat(kafkaClusterProxy.topicExistsInKafkaCluster(correlationId)).isFalse();
      softAssertions.assertThat(topicDaoDynamoDb.getTopicInfo(correlationId)).isEmpty();
    });
  }
}
