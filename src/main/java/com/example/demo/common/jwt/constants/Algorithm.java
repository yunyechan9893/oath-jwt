package com.example.demo.common.jwt.constants;

import lombok.Getter;

public enum Algorithm {
  HS256("HmacSHA256");

  @Getter
  private final String value;

  Algorithm(String value) {
    this.value = value;
  }
}
