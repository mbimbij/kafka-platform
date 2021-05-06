package com.example.topics.infra;

public class EnvironmentVariables {
  private static EnvironmentVariables instance;

  private EnvironmentVariables() {
  }

  public String get(String name) {
    return System.getenv(name);
  }

  public static EnvironmentVariables instance() {
    if (instance == null) {
      instance = new EnvironmentVariables();
    }
    return instance;
  }

  public static void setInstance(EnvironmentVariables instance) {
    EnvironmentVariables.instance = instance;
  }
}
