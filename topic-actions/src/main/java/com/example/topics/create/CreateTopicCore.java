package com.example.topics.create;

import com.example.topics.core.Group;
import com.example.topics.core.KafkaProxy;
import com.example.topics.core.TopicDao;
import com.example.topics.core.User;
import com.example.topics.infra.dao.TopicDaoFactory;
import com.example.topics.infra.kafka.KafkaProxyFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateTopicCore {

  public static final String USER_NOT_IN_GROUP_ERROR_MESSAGE_EXCERPT = "does not belong to the group";
  private final KafkaProxy kafkaProxy;
  private final TopicDao topicDao;

  public CreateTopicResponse createTopic(CreateTopicRequest request){
    validateUserBelongsToSpecifiedOwnerGroup(request.getUser(), request.getTopic().getOwnerGroup());
    kafkaProxy.createTopic(request.getTopic().getName());
    topicDao.saveTopicInfo(request.getTopic());
    return new CreateTopicResponse();
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
