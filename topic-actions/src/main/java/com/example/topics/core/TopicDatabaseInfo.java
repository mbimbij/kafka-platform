package com.example.topics.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class TopicDatabaseInfo {
  private String name;
  private Group ownerGroup;
}
