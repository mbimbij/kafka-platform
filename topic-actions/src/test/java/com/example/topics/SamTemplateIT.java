package com.example.topics;

import com.example.topics.create.CreateTopicHandler;
import com.example.topics.delete.DeleteTopicHandler;
import com.example.topics.delete.lambdaauthorizer.infra.DeletionAuthorizerHandler;
import com.example.topics.details.GetTopicDetailsHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.SneakyThrows;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SamTemplateIT {

  private ObjectMapper mapper;
  private JsonNode jsonNode;

  @SneakyThrows
  @BeforeEach
  void setUp() {
    mapper = new ObjectMapper(new YAMLFactory());
    jsonNode = mapper.readTree(new File("sam-template.yml"));
  }

  @Nested
  public class GetTopicDetailsHandlerSamTest {
    @SneakyThrows
    @Test
    void verifyCodeUri() {
      String handlerName = GetTopicDetailsHandler.class.getSimpleName();
      verifyCodeUriBase(handlerName);
    }

    @SneakyThrows
    @Test
    void verifyHandler() {
      String expectedHandlerFullName = GetTopicDetailsHandler.class.getName();
      String handlerName = GetTopicDetailsHandler.class.getSimpleName();
      verifyHandlerBase(expectedHandlerFullName, handlerName);
    }
  }

  @Nested
  public class CreateTopicHandlerSamTest {
    @SneakyThrows
    @Test
    void verifyCodeUri() {
      String handlerName = CreateTopicHandler.class.getSimpleName();
      verifyCodeUriBase(handlerName);
    }

    @SneakyThrows
    @Test
    void verifyHandler() {
      String expectedHandlerFullName = CreateTopicHandler.class.getName();
      String handlerName = CreateTopicHandler.class.getSimpleName();
      verifyHandlerBase(expectedHandlerFullName, handlerName);
    }
  }

  @Nested
  public class DeleteTopicHandlerSamTest {
    @SneakyThrows
    @Test
    void verifyCodeUri() {
      String handlerName = DeleteTopicHandler.class.getSimpleName();
      verifyCodeUriBase(handlerName);
    }

    @SneakyThrows
    @Test
    void verifyHandler() {
      String expectedHandlerFullName = DeleteTopicHandler.class.getName();
      String handlerName = DeleteTopicHandler.class.getSimpleName();
      verifyHandlerBase(expectedHandlerFullName, handlerName);
    }
  }

  @Nested
  public class DeletionAuthorizerHandlerSamTest {
    @SneakyThrows
    @Test
    void verifyCodeUri() {
      String handlerName = DeletionAuthorizerHandler.class.getSimpleName();
      verifyCodeUriBase(handlerName);
    }

    @SneakyThrows
    @Test
    void verifyHandler() {
      String expectedHandlerFullName = DeletionAuthorizerHandler.class.getName();
      String handlerName = DeletionAuthorizerHandler.class.getSimpleName();
      verifyHandlerBase(expectedHandlerFullName, handlerName);
    }
  }

  private void verifyHandlerBase(String expectedHandlerFullName, String handlerName) {
    // GIVEN
    String actualSamHandlerName = jsonNode.at("/Resources/" + handlerName + "/Properties/Handler").asText();

    // THEN
    assertThat(actualSamHandlerName).isEqualTo(expectedHandlerFullName);
  }

  private void verifyCodeUriBase(String handlerName) throws IOException, XmlPullParserException {
    // GIVEN
    MavenXpp3Reader reader = new MavenXpp3Reader();
    Model model = reader.read(new FileReader("pom.xml"));
    String artifactId = model.getArtifactId();
    String version = model.getVersion();
    String expectedCodeUri = "target/" + artifactId + "-" + version + ".jar";

    // THEN
    String actualCodeUri = jsonNode.at("/Resources/" + handlerName + "/Properties/CodeUri").asText();
    assertThat(actualCodeUri).isEqualTo(expectedCodeUri);
  }

  @SneakyThrows
  @Test
  void name() {
    Optional optional = Optional.empty();
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    String x = objectMapper.writeValueAsString(null);
    System.out.println(x);
  }
}
