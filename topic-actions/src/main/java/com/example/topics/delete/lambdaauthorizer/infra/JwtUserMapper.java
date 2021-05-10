package com.example.topics.delete.lambdaauthorizer.infra;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.topics.core.Group;
import com.example.topics.core.User;

import java.util.List;
import java.util.stream.Collectors;

public class JwtUserMapper {
  public User getUserFromJwt(String jwtString) {
    DecodedJWT decodedJWT = JWT.decode(jwtString);
    List<Group> groups = decodedJWT.getClaim("cognito:groups").asList(String.class).stream()
        .map(Group::new)
        .collect(Collectors.toList());
    String username = decodedJWT.getClaim("cognito:username").asString();
    return User.builder()
        .name(username)
        .groups(groups)
        .build();
  }
}
