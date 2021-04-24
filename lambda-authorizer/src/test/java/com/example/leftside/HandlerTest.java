package com.example.leftside;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;

class HandlerTest {
  @SneakyThrows
  @Test
  void name() {
    Handler handler = new Handler();
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> response = handler.handleRequest(Collections.emptyMap(), null);
    System.out.println(mapper.writeValueAsString(response));
  }
}