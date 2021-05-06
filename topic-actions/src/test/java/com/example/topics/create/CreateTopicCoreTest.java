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
  TopicRepository topicRepository;

  @BeforeEach
  void setUp() {
    topicRepository = mock(TopicRepository.class);
    createTopicCore = new CreateTopicCore(topicRepository);
  }

  @SneakyThrows
  @Test
  void whenUserSendsCreateTopicRequest_thenTopicCreated_andInfoSavedToDatabase() {
    // GIVEN
    Group ownerGroup = new Group("group");
    Topic topicDatabaseInfo = Topic.builder().name(TEST_TOPIC_NAME).ownerGroup(ownerGroup).build();
    User user = User.builder().name("user").groups(Collections.singletonList(ownerGroup)).build();
    CreateTopicRequest request = new CreateTopicRequest(topicDatabaseInfo, user);

    // WHEN
    createTopicCore.createTopic(request);

    // THEN
    verify(topicRepository).create(any(Topic.class));
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
    assertThatThrownBy(() -> createTopicCore.createTopic(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(CreateTopicCore.USER_NOT_IN_GROUP_ERROR_MESSAGE_EXCERPT);

    // THEN
    verify(topicRepository, never()).create(any(Topic.class));
  }

  @SneakyThrows
  @Test
  void whenTopicAlreadyExists_thenDuplicateKeyException() {
    // GIVEN
    Group ownerGroup = new Group("group");
    Topic topic = Topic.builder().name(TEST_TOPIC_NAME).ownerGroup(ownerGroup).build();
    when(topicRepository.get(topic.getName())).thenReturn(Optional.of(topic));

    User user = User.builder().name("user").groups(Collections.singletonList(ownerGroup)).build();
    CreateTopicRequest request = new CreateTopicRequest(topic, user);

    // WHEN
    assertThatThrownBy(() -> createTopicCore.createTopic(request))
        .isInstanceOf(DuplicateEntryException.class)
        .hasMessageContaining(CreateTopicCore.TOPIC_ALREADY_EXISTS_ERROR_MESSAGE_EXCERPT);

    // THEN
    verify(topicRepository, never()).create(any(Topic.class));
  }
}