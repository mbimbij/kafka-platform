package com.example.topics;

import com.amazonaws.services.lambda.runtime.Context;
import com.example.topics.core.TopicRepository;
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
public abstract class BaseLocalDockerIT {
  protected String correlationId;
  protected static final String DYNAMODB_SERVICE_NAME = "dynamodb_1";
  protected static String LOCAL_DYNAMODB_URL;
  protected static DockerComposeContainer<?> dynamoDbContainer =
      new DockerComposeContainer<>(new File("../docker-compose.yml"))
          .withExposedService(DYNAMODB_SERVICE_NAME, 8000)
          .waitingFor(DYNAMODB_SERVICE_NAME,
              Wait
                  .forHttp("/shell")
                  .withStartupTimeout(Duration.ofSeconds(30)));
  protected final Context testContext = TestContext.builder().build();
  protected static DynamoDbEnhancedClient dynamoDbEnhancedClient;
  protected static TopicRepository topicRepository;
  protected static KafkaClusterProxy kafkaClusterProxy;
  protected static TopicDaoDynamoDb topicDaoDynamoDb;
  protected static final EnvironmentVariables environmentVariables = spy(EnvironmentVariables.instance());
  protected final ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());

  static {
    dynamoDbContainer.start();
  }

  @BeforeAll
  public static void beforeAllBase() {
    log.info("before");
    setEnvVars();
    setDynamoDbLocalUrl();
    initDynamoDbLocalClient();
    createTopicInfoTableIfNotExists();
    topicDaoDynamoDb = TopicDaoDynamoDbFactory.buildTopicDaoDynamoDb();
    kafkaClusterProxy = KafkaClusterProxyFactory.buildKafkaClusterProxy();
    topicRepository = TopicRepositoryFactory.buildTopicRepositoryFactory();
  }

  @BeforeEach
  public void setUpBase() {
    correlationId = UUID.randomUUID().toString();
  }

  private static void setEnvVars() {
    EnvironmentVariables.setInstance(environmentVariables);
    setEnvVarIfNotDefined("BOOTSTRAP_SERVERS", "localhost:9092");
    setEnvVarIfNotDefined("TOPIC_CREATION_TIMEOUT_MILLIS", "20000");
  }

  @AfterAll
  static void afterAllBase() {
    log.info("afterAll BaseLocalDockerIT");
  }

  private static void setEnvVarIfNotDefined(String name, String valueIfAbsent) {
    if (Objects.isNull(System.getenv(name))) {
      when(environmentVariables.get(name)).thenReturn(valueIfAbsent);
      System.out.println();
    } else {
      log.info(name + " already defined");
    }
    log.info(name + "={}", EnvironmentVariables.instance().get(name));
  }

  protected static void setDynamoDbLocalUrl() {
    int dynamodbMappedPort = dynamoDbContainer.getServicePort(DYNAMODB_SERVICE_NAME, 8000);
    LOCAL_DYNAMODB_URL = String.format("http://localhost:%d", dynamodbMappedPort);
  }

  protected static void initDynamoDbLocalClient() {
    try (MockedStatic<TopicDaoDynamoDbFactory> mocked = mockStatic(TopicDaoDynamoDbFactory.class)) {
      mocked.when(TopicDaoDynamoDbFactory::createDynamoDbEnhancedClient).thenReturn(createLocalDynamoDBEnhancedClient());
    }
    dynamoDbEnhancedClient = TopicDaoDynamoDbFactory.createDynamoDbEnhancedClient();
  }

  protected static void createTopicInfoTableIfNotExists() {
    try {
      dynamoDbEnhancedClient.table(TopicDaoDynamoDb.TABLE_NAME, TableSchema.fromBean(TopicEntity.class)).createTable();
    } catch (ResourceInUseException e) {
      log.warn(e.getMessage());
    }
  }

  protected static DynamoDbEnhancedClient createLocalDynamoDBEnhancedClient() {
    DynamoDbEnhancedClient enhancedClient =
        DynamoDbEnhancedClient.builder()
            .dynamoDbClient(createClient())
            .build();
    return enhancedClient;
  }

  protected static DynamoDbClient createClient() {
    return DynamoDbClient.builder()
        .endpointOverride(URI.create(LOCAL_DYNAMODB_URL))
        // The region is meaningless for local DynamoDb but required for client builder validation
        .region(Region.EU_WEST_3)
//        .credentialsProvider(StaticCredentialsProvider.create(
//            AwsBasicCredentials.create("dummy-key", "dummy-secret")))
        .build();
  }
}
