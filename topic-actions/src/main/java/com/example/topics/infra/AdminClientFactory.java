package com.example.topics.infra;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

import java.util.Properties;

@Slf4j
public class AdminClientFactory {
  public static AdminClient createAdminClient(){
    Properties config = new Properties();
    String bootstrapServers = EnvironmentVariables.instance().get("BOOTSTRAP_SERVERS");
    log.info("bootstrapServers: {}", bootstrapServers);
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    return AdminClient.create(config);
  }
}
