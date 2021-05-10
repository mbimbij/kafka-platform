package com.example.topics;

import com.amazonaws.services.lambda.runtime.Context;
import com.example.topics.core.TopicRepository;
import com.example.topics.delete.lambdaauthorizer.infra.DeletionAuthorizerHandler;
import com.example.topics.details.GetTopicDetailsHandler;
import com.example.topics.infra.EnvironmentVariables;
import com.example.topics.infra.TopicRepositoryFactory;
import com.example.topics.infra.dynamodb.TopicDaoDynamoDb;
import com.example.topics.infra.dynamodb.TopicDaoDynamoDbFactory;
import com.example.topics.infra.dynamodb.TopicEntity;
import com.example.topics.infra.kafka.KafkaClusterProxy;
import com.example.topics.infra.kafka.KafkaClusterProxyFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.Mockito.*;

@Slf4j
public abstract class BaseHandlerTest {
  protected final Context testContext = TestContext.builder().build();
  protected static TopicRepository topicRepository;
  protected static KafkaClusterProxy kafkaClusterProxy;
  protected static TopicDaoDynamoDb topicDaoDynamoDb;
  protected static DeletionAuthorizerHandler deletionAuthorizerHandler;
  protected EnvironmentVariables environmentVariables;
  protected final ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());

  @BeforeEach
  public void beforeEachBase() {
    topicRepository = mock(TopicRepository.class);
    kafkaClusterProxy = mock(KafkaClusterProxy.class);
    topicDaoDynamoDb = mock(TopicDaoDynamoDb.class);
    mockEnvironmentVariales();
    try (MockedStatic<TopicRepositoryFactory> topicRepositoryFactoryMockedStatic = mockStatic(TopicRepositoryFactory.class);
         MockedStatic<KafkaClusterProxyFactory> kafkaClusterProxyFactoryMockedStatic = mockStatic(KafkaClusterProxyFactory.class);
         MockedStatic<TopicDaoDynamoDbFactory> topicDaoDynamoDbFactoryMockedStatic = mockStatic(TopicDaoDynamoDbFactory.class)) {
      topicRepositoryFactoryMockedStatic.when(TopicRepositoryFactory::buildTopicRepositoryFactory).thenReturn(topicRepository);
      kafkaClusterProxyFactoryMockedStatic.when(KafkaClusterProxyFactory::buildKafkaClusterProxy).thenReturn(kafkaClusterProxy);
      topicDaoDynamoDbFactoryMockedStatic.when(TopicDaoDynamoDbFactory::buildTopicDaoDynamoDb).thenReturn(topicDaoDynamoDb);
      deletionAuthorizerHandler = new DeletionAuthorizerHandler();
    }
  }

  private void mockEnvironmentVariales() {
    environmentVariables = spy(EnvironmentVariables.instance());
    EnvironmentVariables.setInstance(environmentVariables);
    setEnvVarIfNotDefined("AWS_REGION", "eu-west-3");
    setEnvVarIfNotDefined("ACCOUNT_ID", "someAccountId");
    setEnvVarIfNotDefined("API_LIVE_STAGE_URL", "someApiLiveStageUrl");
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
