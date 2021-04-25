package com.example.authorization.infra;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Coordinates {
  private byte red;

  @JsonProperty("ruh")
  public byte getRpoi() {
    return red;
  }

  @JsonProperty("red")
  public void setRpoijuh(byte red) {
    this.red = red;
  }
}

