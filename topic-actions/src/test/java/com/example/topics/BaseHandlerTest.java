package com.example.topics;

import com.amazonaws.services.lambda.runtime.Context;
import com.example.topics.core.TopicRepository;
import com.example.topics.infra.EnvironmentVariables;
import com.example.topics.infra.TopicRepositoryFactory;
import com.example.topics.infra.dynamodb.TopicDaoDynamoDb;
import com.example.topics.infra.dynamodb.TopicDaoDynamoDbFactory;
import com.example.topics.infra.kafka.KafkaClusterProxy;
import com.example.topics.infra.kafka.KafkaClusterProxyFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;

import java.util.Objects;

import static org.mockito.Mockito.*;

@Slf4j
public abstract class BaseHandlerTest {
  protected final Context testContext = TestContext.builder().build();
  protected static TopicRepository topicRepository;
  protected static KafkaClusterProxy kafkaClusterProxy;
  protected static TopicDaoDynamoDb topicDaoDynamoDb;
  protected EnvironmentVariables environmentVariables;
  protected final ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());

  @BeforeEach
  public void beforeEachBase() {
    mockEnvironmentVariales();
    mockTopicRepositoryFactory();
    mockKafkaClusterProxyFactory();
    mockTopicDaoDynamoDbFactory();
  }

  private void mockTopicDaoDynamoDbFactory() {
    topicDaoDynamoDb = mock(TopicDaoDynamoDb.class);
    TopicDaoDynamoDbFactory topicDaoDynamoDbFactory = mock(TopicDaoDynamoDbFactory.class);
    TopicDaoDynamoDbFactory.setInstance(topicDaoDynamoDbFactory);
    when(topicDaoDynamoDbFactory.buildTopicDaoDynamoDb()).thenReturn(topicDaoDynamoDb);
  }

  private void mockKafkaClusterProxyFactory() {
    kafkaClusterProxy = mock(KafkaClusterProxy.class);
    KafkaClusterProxyFactory kafkaClusterProxyFactory = mock(KafkaClusterProxyFactory.class);
    KafkaClusterProxyFactory.setInstance(kafkaClusterProxyFactory);
    when(kafkaClusterProxyFactory.buildKafkaClusterProxy()).thenReturn(kafkaClusterProxy);
  }

  private void mockTopicRepositoryFactory() {
    topicRepository = mock(TopicRepository.class);
    TopicRepositoryFactory topicRepositoryFactory = mock(TopicRepositoryFactory.class);
    TopicRepositoryFactory.setInstance(topicRepositoryFactory);
    when(topicRepositoryFactory.buildTopicRepositoryFactory()).thenReturn(topicRepository);
  }

  private void mockEnvironmentVariales() {
    environmentVariables = spy(EnvironmentVariables.instance());
    EnvironmentVariables.setInstance(environmentVariables);
    setEnvVarIfNotDefined("AWS_REGION", "eu-west-3");
    setEnvVarIfNotDefined("ACCOUNT_ID", "someAccountId");
  }

  private void setEnvVars() {
    EnvironmentVariables.setInstance(environmentVariables);
    setEnvVarIfNotDefined("BOOTSTRAP_SERVERS", "localhost:9092");
    setEnvVarIfNotDefined("TOPIC_CREATION_TIMEOUT_MILLIS", "20000");
  }

  protected void setEnvVarIfNotDefined(String name, String valueIfAbsent) {
    if (Objects.isNull(System.getenv(name))) {
      when(environmentVariables.get(name)).thenReturn(valueIfAbsent);
      System.out.println();
    } else {
      log.info(name + " already defined");
    }
    log.info(name + "={}", EnvironmentVariables.instance().get(name));
  }
}
