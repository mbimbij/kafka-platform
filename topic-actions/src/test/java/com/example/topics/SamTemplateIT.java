package com.example.topics;

import com.example.topics.details.GetTopicDetailsHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.SneakyThrows;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;

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

  @SneakyThrows
  @Test
  void verifyCodeUri_andHandler() {
    // GIVEN
    MavenXpp3Reader reader = new MavenXpp3Reader();
    Model model = reader.read(new FileReader("pom.xml"));
    String artifactId = model.getArtifactId();
    String version = model.getVersion();
    String expectedCodeUri = "target/" + artifactId + "-" + version + ".jar";

    // THEN
    String actualCodeUri = jsonNode.at("/Resources/GetTopicDetailsHandler/Properties/CodeUri").asText();
    assertThat(actualCodeUri).isEqualTo(expectedCodeUri);
  }

  @SneakyThrows
  @Test
  void verifyHandler() {
    // GIVEN
    String expectedSamHandlerName = GetTopicDetailsHandler.class.getName();
    String actualSamHandlerName = jsonNode.at("/Resources/GetTopicDetailsHandler/Properties/Handler").asText();

    // THEN
    assertThat(actualSamHandlerName).isEqualTo(expectedSamHandlerName);
  }
}
