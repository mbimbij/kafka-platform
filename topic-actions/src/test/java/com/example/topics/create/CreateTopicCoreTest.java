package com.example.topics.create;

import com.example.topics.core.*;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.mockito.Mockito.*;

class CreateTopicCoreTest {

  private static final String TEST_TOPIC_NAME = "testTopic";
  private CreateTopicCore createTopicCore;
  private KafkaProxy kafkaProxy;
  private TopicDao topicDao;

  @BeforeEach
  void setUp() {
    kafkaProxy = mock(KafkaProxy.class);
    topicDao = mock(TopicDao.class);
    createTopicCore = new CreateTopicCore(kafkaProxy, topicDao);
  }

  @SneakyThrows
  @Test
  void whenUserSendsCreateTopicRequest_thenTopicCreated_andInfoSavedToDatabase() {
    // GIVEN
    Group ownerGroup = new Group("group");
    Topic topic = Topic.builder().name(TEST_TOPIC_NAME).ownerGroup(ownerGroup).build();
    User user = User.builder().name("user").groups(Collections.singletonList(ownerGroup)).build();
    CreateTopicRequest request = new CreateTopicRequest(topic, user);

    // WHEN
    createTopicCore.createTopic(request);

    // THEN
    verify(topicDao).saveTopicInfo(any(Topic.class));
    verify(kafkaProxy).createTopic(TEST_TOPIC_NAME);
  }

  @SneakyThrows
  @Test
  void whenUserDoesNotBelongToGroupRequested_thenIllegalArgumentException_andTopicNotCreated_andTopicInfoNotPersistedToDatabase() {
    // GIVEN
    Group requestedOwnerGroup = new Group("group");
    Group anotherGroup = new Group("anotherGroup");
    Topic topic = Topic.builder().name(TEST_TOPIC_NAME).ownerGroup(requestedOwnerGroup).build();
    User user = User.builder().name("user").groups(Collections.singletonList(anotherGroup)).build();
    CreateTopicRequest request = new CreateTopicRequest(topic, user);

    // WHEN
    Assertions.assertThatThrownBy(() -> createTopicCore.createTopic(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(CreateTopicCore.USER_NOT_IN_GROUP_ERROR_MESSAGE_EXCERPT);

    // THEN
    verify(topicDao, never()).saveTopicInfo(any(Topic.class));
    verify(kafkaProxy, never()).createTopic(anyString());
  }
}