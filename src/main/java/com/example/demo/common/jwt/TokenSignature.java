package com.example.demo.common.jwt;

public record TokenSignature(String signature) {

  public boolean validate(String signature) {
    return signature.equals(this.signature);
  }
}
