package com.example.demo.common.jwt;

import com.example.demo.common.jwt.constants.Algorithm;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenUtilTest {

  private static final String VALID_SECRET_KEY = "access_token";
  private static final String INVALID_SECRET_KEY = "invalid_access_token";

  @Test
  void 토큰완성테스트() {
    TokenHeader tokenHeader = new TokenHeader(Algorithm.HS256, "jwt");
    TokenPayload tokenPayload = new TokenPayload(0, LocalDateTime.now(), "access_token");

    Token token = new Token(tokenHeader, tokenPayload, VALID_SECRET_KEY);
    String tokenBase64 = token.getEncodingToken();

    assert !tokenBase64.isEmpty();

  }

  @Test
  void 토큰해독테스트() {
    TokenHeader tokenHeader = new TokenHeader(Algorithm.HS256, "jwt");
    TokenPayload tokenPayload = new TokenPayload(0, LocalDateTime.now(), "access_token");

    Token token = new Token(tokenHeader, tokenPayload, VALID_SECRET_KEY);
    String tokenBase64 = token.getEncodingToken();
    Token decodedToken = new Token(tokenBase64);

    Assertions.assertThat(decodedToken).isEqualTo(token);
  }

  @Test
  void 만료된토큰테스트() {
    TokenHeader tokenHeader = new TokenHeader(Algorithm.HS256, "jwt");
    TokenPayload tokenPayload = new TokenPayload(0, LocalDateTime.now(), "access_token");

    Token issueToken = new Token(tokenHeader, tokenPayload, VALID_SECRET_KEY);
    assertThrows(RuntimeException.class, () -> issueToken.validate(VALID_SECRET_KEY));
  }

  @Test
  void 토큰유효성성공테스트() {
    TokenHeader tokenHeader = new TokenHeader(Algorithm.HS256, "jwt");
    TokenPayload tokenPayload = new TokenPayload(0, LocalDateTime.now().plusHours(1), "access_token");

    Token issueToken = new Token(tokenHeader, tokenPayload, VALID_SECRET_KEY);
    assertDoesNotThrow(() -> issueToken.validate(VALID_SECRET_KEY));
  }

  @Test
  void 토큰유효성실패테스트() {
    TokenHeader tokenHeader = new TokenHeader(Algorithm.HS256, "jwt");
    TokenPayload tokenPayload = new TokenPayload(0, LocalDateTime.now().plusHours(1), "access_token");

    Token issueToken = new Token(tokenHeader, tokenPayload, INVALID_SECRET_KEY);
    assertThrows(RuntimeException.class, () -> issueToken.validate(VALID_SECRET_KEY));
  }
}