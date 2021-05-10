package com.example.topics.delete.lambdaauthorizer.infra;

import com.example.topics.core.Group;
import com.example.topics.core.User;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JwtUserMapperTest {
  @Test
  void userFromJwtTest() {
    // GIVEN
    JwtUserMapper jwtUserMapper = new JwtUserMapper();
    String encodedJwt = "eyJraWQiOiJRbitoOXNVTjVQWU1PVWt3SlZKR0RtZHd4YmpSbitNakhDN21rMHRnS3UwPSIsImFsZyI6IlJTMjU2In0.eyJhdF9oYXNoIjoiUEZKbG1EN3ZSMzFQaU0wYTVEdkh3ZyIsInN1YiI6IjcxOTM5YTA2LTdiMzktNGVhZC1hYjRhLWQwZDIxOWVlMDU4ZCIsImF1ZCI6IjV2MmpqbWl0ZmRybm9wbmJubWlzczVpc2VsIiwiY29nbml0bzpncm91cHMiOlsiZGV2Il0sImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTYxOTI3Njk3NCwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLmV1LXdlc3QtMy5hbWF6b25hd3MuY29tXC9ldS13ZXN0LTNfSE5KcllSaWoxIiwiY29nbml0bzp1c2VybmFtZSI6Impvc2VwaCIsImV4cCI6MTYxOTI4MDU3NCwiaWF0IjoxNjE5Mjc2OTc0LCJlbWFpbCI6Impvc2VwaC5tYmltYmlAZ21haWwuY29tIn0.uGk63nxmhTTXD2r6ukL0jmJZiajoqJCSueHdbPnbIkJeTCTWwCzRgLDnejOwIkd6x5cJi-wTHEn8oP4N9RMY2Mb8dkrgijl8FGhUjWGNKIvHZ3p0aCPqSVX28XUIO9S1REUis1zZ7vweACbP7fvmWS7PsRmH6Mt_opkqCzr_q8IqtaE9RE7uNbPMcZFyMhMCexAaIrtZeTqKNj4ydjaHkYEjyTplNOrJAns38dh1l2Tyj3ataOTPHe9iD40sskPannWfacs7Vw9mw7SKxO3twij8W7-kCNjYYtVUwTJNaTiJqAPa9721xmg4MiUosY_-WjOWqBM_ErC4bJmvDYDZoA";
    User expectedUser = User.builder().name("joseph").groups(Arrays.asList("dev").stream().map(Group::new).collect(Collectors.toList())).build();

    // WHEN
    User actualUser = jwtUserMapper.getUserFromJwt(encodedJwt);

    // THEN
    assertThat(actualUser).isEqualTo(expectedUser);
  }
}