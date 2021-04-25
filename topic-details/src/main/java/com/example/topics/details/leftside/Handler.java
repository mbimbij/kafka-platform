package com.example.topics.details.leftside;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class Handler implements RequestHandler<Map<String, Object>, Object> {

  @SneakyThrows
  @Override
  public Object handleRequest(Map<String, Object> request, Context context) {
    return "hello topics";
  }
}
