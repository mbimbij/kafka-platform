package com.example.topics.core;

public class DuplicateEntryException extends RuntimeException {
  public DuplicateEntryException(String message) {
    super(message);
  }
}
