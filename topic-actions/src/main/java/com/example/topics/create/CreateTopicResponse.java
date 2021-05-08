package com.example.topics.create;

import com.example.topics.core.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTopicResponse {
  private String name;
  private Group ownerGroup;
}
