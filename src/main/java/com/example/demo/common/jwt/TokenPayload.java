package com.example.demo.common.jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@ToString
public class TokenPayload {

  private final String id;
  private final int permission;
  private final String expires;
  private final String subject;
  private final String role;
  private final Map<String, Object> payload = new HashMap<>();

  public TokenPayload(
      int permission,
      LocalDateTime expires,
      String subject,
      String role) {
    id = UUID.randomUUID().toString();
    this.permission = permission;
    this.expires = expires.toString();
    this.subject = subject;
    this.role = role;
  }

  @JsonCreator
  public TokenPayload(
      @JsonProperty("id") String id,
      @JsonProperty("permission") int permission,
      @JsonProperty("expires") String expires,
      @JsonProperty("subject") String subject,
      @JsonProperty("role") String role) {
    this.id = id;
    this.permission = permission;
    this.expires = expires;
    this.subject = subject;
    this.role = role;
  }

  @JsonCreator
  public TokenPayload(String serializedTokenHeader) {
    byte[] headerBytes = decodeBase64(serializedTokenHeader);
    TokenPayload header = deserialize(headerBytes);
    id = header.getId();
    permission = header.getPermission();
    expires = header.getExpires();
    subject = header.getSubject();
    role = header.getRole();
  }

  private byte[] decodeBase64(String tokenBase64) {
    return Base64.getDecoder().decode(tokenBase64);
  }

  private TokenPayload deserialize(byte[] jsonBytes) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(jsonBytes, TokenPayload.class);
    } catch (IOException e) {
      throw new RuntimeException("역직렬화에 실패했습니다.", e);
    }
  }

  @JsonIgnore
  public String getBase64() {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      String headerJson = objectMapper.writeValueAsString(this);
      return Base64.getEncoder().encodeToString(headerJson.getBytes());
    } catch (JsonProcessingException e) {
      throw new RuntimeException("직렬화의 실패했습니다.");
    }
  }

  @JsonIgnore
  public boolean isExpirePeriod() {
    LocalDateTime expireAt = LocalDateTime.parse(expires);
    return expireAt.isBefore(LocalDateTime.now());
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    TokenPayload that = (TokenPayload) o;
    return permission == that.permission && Objects.equals(id, that.id) && Objects.equals(expires, that.expires) && Objects.equals(subject, that.subject);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, permission, expires, subject);
  }
}
