package com.example.topics.core;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GroupTest {
  @Test
  void whenGroupsNameAreEqual_thenGroupsAreEqual() {
    Group group1 = new Group("group1");
    Group group2 = new Group("group1");

    //THEN
    assertThat(group1).isEqualTo(group2);
  }

  @Test
  void whenGroupsNameAreNotEqual_thenGroupsAreNotEqual() {
    Group group1 = new Group("group1");
    Group group2 = new Group("group2");

    //THEN
    assertThat(group1).isNotEqualTo(group2);
  }
}