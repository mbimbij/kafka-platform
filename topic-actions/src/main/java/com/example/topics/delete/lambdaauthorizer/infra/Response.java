package com.example.topics.delete.lambdaauthorizer.infra;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
  private String principalId;
  private PolicyDocument policyDocument;

  public PolicyDocument getPolicyDocument() {
    return policyDocument;
  }

  public void setPolicyDocument(PolicyDocument policyDocument) {
    this.policyDocument = policyDocument;
  }

  @Data
  private static class PolicyDocument {
    @JsonProperty("Version")
    String Version;

    @JsonProperty("Statement")
    List<Statement> Statement;

    @Data
    private static class Statement {
      @JsonProperty("Action")
      String Action;
      @JsonProperty("Resource")
      List<String> Resource;
      @JsonProperty("Effect")
      String Effect;
    }
  }
}
