package com.example.leftside;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class CoordinatesTest {

  @SneakyThrows
  @Test
  void name() {
    Coordinates c = new Coordinates();
    c.setRpoijuh((byte) 5);

    ObjectMapper mapper = new ObjectMapper();
    System.out.println("Serialization: " + mapper.writeValueAsString(c));

    Coordinates r = mapper.readValue("{\"red\":25}",Coordinates.class);
    System.out.println("Deserialization: " + mapper.writeValueAsString(r));
  }
}