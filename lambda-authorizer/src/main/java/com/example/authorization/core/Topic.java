package com.example.authorization.core;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Topic {
  private String name;
  private Group ownerGroup;
}
