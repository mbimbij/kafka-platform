package com.example.leftside;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.core.Group;
import com.example.core.User;

import java.util.List;
import java.util.stream.Collectors;

public class JwtUserMapper {
  public User getUserFromJwt(String jwtString){
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
