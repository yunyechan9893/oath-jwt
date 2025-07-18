package com.example.demo.common.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenComponent {

  @Value("${spring.jwt.secret-key}")
  private String secretKey;

  public Token createToken(TokenHeader header, TokenPayload payload) {
    return new Token(header, payload, secretKey);
  }

  public Token decodeToken(String token) {
    return new Token(token);
  }

}
