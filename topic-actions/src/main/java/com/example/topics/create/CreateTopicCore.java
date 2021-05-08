package com.example.topics.create;

import com.example.topics.core.*;
import com.example.topics.infra.TopicRepositoryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateTopicCore {
  public static final String USER_NOT_IN_GROUP_ERROR_MESSAGE_EXCERPT = "does not belong to the group";
  public static final String TOPIC_ALREADY_EXISTS_ERROR_MESSAGE_EXCERPT = "topic already exists:";
  private final TopicRepository topicRepository;

  public CreateTopicResponse createTopic(CreateTopicRequest request) {
    validateUserBelongsToSpecifiedOwnerGroup(request.getUser(), request.getTopic().getOwnerGroup());
    validateTopicNotAlreadyExists(request);
    Topic topic = Topic.builder().name(request.getTopic().getName()).ownerGroup(request.getTopic().getOwnerGroup()).build();
    topicRepository.create(topic);
    return new CreateTopicResponse(topic.getName(), topic.getOwnerGroup());
  }

  private void validateTopicNotAlreadyExists(CreateTopicRequest request) {
    if (topicRepository.get(request.getTopic().getName()).isPresent()) {
      throw new DuplicateEntryException(TOPIC_ALREADY_EXISTS_ERROR_MESSAGE_EXCERPT + " \"" + request.getTopic().getName() + "\"");
    }
  }

  private void validateUserBelongsToSpecifiedOwnerGroup(User user, Group ownerGroup) {
    if (!user.getGroups().contains(ownerGroup)) {
      throw new IllegalArgumentException("user \"" + user.getName() + "\" " + USER_NOT_IN_GROUP_ERROR_MESSAGE_EXCERPT + " \"" + ownerGroup
          + "\" and as such cannot create a topic with that group as its owner");
    }
  }

  public static CreateTopicCore createInstance() {
    return new CreateTopicCore(TopicRepositoryFactory.buildTopicRepositoryFactory());
  }
}
