package com.example.topics.create;

import com.example.topics.core.*;
import com.example.topics.infra.dao.TopicDaoFactory;
import com.example.topics.infra.kafka.KafkaProxyFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateTopicCore {
  public static final String USER_NOT_IN_GROUP_ERROR_MESSAGE_EXCERPT = "does not belong to the group";
  private final KafkaProxy kafkaProxy;
  private final TopicDao topicDao;
  public static final  String TOPIC_ALREADY_EXISTS_ERROR_MESSAGE_EXCERPT = "topic already exists:";

  public CreateTopicResponse createTopic(CreateTopicRequest request){
    validateUserBelongsToSpecifiedOwnerGroup(request.getUser(), request.getTopic().getOwnerGroup());
    validateTopicNotAlreadyExists(request);
    kafkaProxy.createTopic(request.getTopic().getName());
    topicDao.saveTopicInfo(request.getTopic());
    return new CreateTopicResponse();
  }

  private void validateTopicNotAlreadyExists(CreateTopicRequest request) {
    if(topicDao.getTopicInfo(request.getTopic().getName()).isPresent()){
      throw new DuplicateEntryException(TOPIC_ALREADY_EXISTS_ERROR_MESSAGE_EXCERPT + " \"" + request.getTopic().getName()+"\"");
    }
  }

  private void validateUserBelongsToSpecifiedOwnerGroup(User user, Group ownerGroup) {
    if(!user.getGroups().contains(ownerGroup)){
      throw new IllegalArgumentException("user \""+ user.getName()+ "\" " + USER_NOT_IN_GROUP_ERROR_MESSAGE_EXCERPT + " \"" + ownerGroup
          +"\" and as such cannot create a topic with that group as its owner");
    }
  }

  public static CreateTopicCore createInstance(){
    return new CreateTopicCore(KafkaProxyFactory.createKafkaProxy(), TopicDaoFactory.buildTopicDao());
  }
}
