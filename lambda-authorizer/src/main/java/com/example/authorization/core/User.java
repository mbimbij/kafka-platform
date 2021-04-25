package com.example.authorization.core;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class User {
  private String name;
  private List<Group> groups;
}
