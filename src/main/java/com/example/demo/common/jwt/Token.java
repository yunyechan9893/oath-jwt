package com.example.demo.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.ToString;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

@Getter
@ToString
public class Token {

  private static final String DOT = ".";
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final TokenHeader header;
  private final TokenPayload payload;
  private final TokenSignature signature;

  public Token(TokenHeader header, TokenPayload payload, String secretKey) {
    this.header = header;
    this.payload = payload;

    try {
      String tokenBase64 = header.getBase64() + DOT + payload.getBase64();
      String encodingSignature = getEncodingSignature(secretKey, tokenBase64);
      signature = new TokenSignature(encodingSignature);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException(e);
    }
  }

  public Token(String token) {
    String[] tokenParts = token.split("\\.");

    if (tokenParts.length != 3) {
      throw new RuntimeException("유효하지 않은 토큰입니다.");
    }

    header = new TokenHeader(tokenParts[0]);
    payload = new TokenPayload(tokenParts[1]);
    signature = new TokenSignature(tokenParts[2]);
  }

  private String getEncodingSignature(String secretKey, String tokenBase64) throws NoSuchAlgorithmException, InvalidKeyException {
    SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), header.getAlg().getValue());
    Mac mac = Mac.getInstance(header.getAlg().getValue());
    mac.init(secretKeySpec);
    byte[] signatureBytes = mac.doFinal(tokenBase64.getBytes());

    return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
  }

  public String getEncodingToken() {
    return header.getBase64() + DOT + payload.getBase64() + DOT + signature.signature();
  }

  public void validate(String secretKey) {
    if (payload.isExpirePeriod()) {
      throw new RuntimeException("토큰이 만료됐습니다");
    }

    Token token = new Token(header, payload, secretKey);

    boolean isValidToken = signature.validate(token.signature.signature());
    if (!isValidToken) {
      throw new RuntimeException("유효하지 않은 토큰입니다");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Token token = (Token) o;
    return Objects.equals(header, token.header) && Objects.equals(payload, token.payload);
  }

  @Override
  public int hashCode() {
    return Objects.hash(header, payload);
  }
}
