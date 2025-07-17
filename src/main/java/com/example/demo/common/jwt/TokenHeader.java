package com.example.demo.common.jwt;

import com.example.demo.common.jwt.constants.Algorithm;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@Getter
@ToString
public class TokenHeader {

  private final Algorithm alg;
  private final String typ;

  @JsonCreator
  public TokenHeader(
      @JsonProperty("alg") Algorithm alg,
      @JsonProperty("typ") String typ) {
    this.alg = alg;
    this.typ = typ;
  }

  @JsonCreator
  public TokenHeader(String serializedTokenHeader) {
    byte[] headerBytes = decodeBase64(serializedTokenHeader);
    TokenHeader header = deserialize(headerBytes);
    alg = header.getAlg();
    typ = header.getTyp();
  }

  private byte[] decodeBase64(String tokenBase64) {
    return Base64.getDecoder().decode(tokenBase64);
  }

  private TokenHeader deserialize(byte[] jsonBytes) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(jsonBytes, TokenHeader.class);
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

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    TokenHeader that = (TokenHeader) o;
    return alg == that.alg && Objects.equals(typ, that.typ);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alg, typ);
  }
}
