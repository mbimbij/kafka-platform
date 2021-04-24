package com.example.core;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Topic {
  private String name;
  private Group ownerGroup;
}
