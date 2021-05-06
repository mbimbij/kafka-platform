package com.example.topics.create;

import com.example.topics.core.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    TopicDatabaseInfo topicDatabaseInfo = TopicDatabaseInfo.builder().name(TEST_TOPIC_NAME).ownerGroup(ownerGroup).build();
    User user = User.builder().name("user").groups(Collections.singletonList(ownerGroup)).build();
    CreateTopicRequest request = new CreateTopicRequest(topicDatabaseInfo, user);

    // WHEN
    createTopicCore.createTopic(request);

    // THEN
    verify(topicDao).saveTopicInfo(any(TopicDatabaseInfo.class));
    verify(kafkaProxy).createTopic(TEST_TOPIC_NAME);
  }

  @SneakyThrows
  @Test
  void whenUserDoesNotBelongToGroupRequested_thenIllegalArgumentException_andTopicNotCreated_andTopicInfoNotPersistedToDatabase() {
    // GIVEN
    Group requestedOwnerGroup = new Group("group");
    Group anotherGroup = new Group("anotherGroup");
    TopicDatabaseInfo topicDatabaseInfo = TopicDatabaseInfo.builder().name(TEST_TOPIC_NAME).ownerGroup(requestedOwnerGroup).build();
    User user = User.builder().name("user").groups(Collections.singletonList(anotherGroup)).build();
    CreateTopicRequest request = new CreateTopicRequest(topicDatabaseInfo, user);

    // WHEN
    assertThatThrownBy(() -> createTopicCore.createTopic(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(CreateTopicCore.USER_NOT_IN_GROUP_ERROR_MESSAGE_EXCERPT);

    // THEN
    verify(topicDao, never()).saveTopicInfo(any(TopicDatabaseInfo.class));
    verify(kafkaProxy, never()).createTopic(anyString());
  }

  @SneakyThrows
  @Test
  void whenTopicAlreadyExists_thenDuplicateKeyException() {
    // GIVEN
    Group ownerGroup = new Group("group");
    TopicDatabaseInfo topicDatabaseInfo = TopicDatabaseInfo.builder().name(TEST_TOPIC_NAME).ownerGroup(ownerGroup).build();
    when(topicDao.getTopicInfo(topicDatabaseInfo.getName())).thenReturn(Optional.of(topicDatabaseInfo));

    User user = User.builder().name("user").groups(Collections.singletonList(ownerGroup)).build();
    CreateTopicRequest request = new CreateTopicRequest(topicDatabaseInfo, user);

    // WHEN
    assertThatThrownBy(() -> createTopicCore.createTopic(request))
        .isInstanceOf(DuplicateEntryException.class)
        .hasMessageContaining(CreateTopicCore.TOPIC_ALREADY_EXISTS_ERROR_MESSAGE_EXCERPT);

    // THEN
    verify(topicDao, never()).saveTopicInfo(any(TopicDatabaseInfo.class));
    verify(kafkaProxy, never()).createTopic(anyString());
  }
}