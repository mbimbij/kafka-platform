package com.example.topics.delete.lambdaauthorizer.infra;

import com.example.topics.core.Topic;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

public class TopicDetailsGateway {

  private final Region REGION = Region.EU_WEST_3;
  private final String FUNCTION_NAME = "topic-details";

  @SneakyThrows
  public Topic getTopicDetails(String topicName) {
    try (LambdaClient awsLambda = LambdaClient.builder()
        .region(REGION)
        .build()) {

      //Setup an InvokeRequest
      InvokeRequest request = InvokeRequest.builder()
          .functionName(FUNCTION_NAME)
          .payload(SdkBytes.fromUtf8String("{\"topicName\": \"" + topicName + "\"}"))
          .build();

      //Invoke the Lambda function
      String responseString = awsLambda.invoke(request).payload().asUtf8String();
      return new ObjectMapper().readValue(responseString, Topic.class);
    }
  }
}
