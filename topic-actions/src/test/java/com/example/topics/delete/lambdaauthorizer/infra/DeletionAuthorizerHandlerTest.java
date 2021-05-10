package com.example.topics.delete.lambdaauthorizer.infra;

import com.example.topics.BaseHandlerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeletionAuthorizerHandlerTest extends BaseHandlerTest {

  private static final String API_LIVE_STAGE_URL = "someApiLiveStageUrl";

  @BeforeEach
  void setUp() {

  }

  @Test
  void givenTopicExists_whenDeletionCalledByOwner_thenAuthorized() {
    System.out.println();
  }
}