package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class Handler implements RequestHandler<Object, Object> {

  public final static String RESPONSE = "hello - v1";

  @Override
  public Object handleRequest(Object helloRequest, Context context) {
    log.info(RESPONSE);
    return RESPONSE;
  }
}
