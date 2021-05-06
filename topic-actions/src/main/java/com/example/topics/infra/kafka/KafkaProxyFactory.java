package com.example.topics.infra.kafka;

import com.example.topics.core.KafkaProxy;

public class KafkaProxyFactory {
  public static KafkaProxy createKafkaProxy(){
    return new KafkaProxyImpl();
  }
}
