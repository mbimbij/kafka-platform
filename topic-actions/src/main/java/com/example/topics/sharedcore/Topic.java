package com.example.topics.sharedcore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class Topic {
  private String name;
  private Group ownerGroup;
}
