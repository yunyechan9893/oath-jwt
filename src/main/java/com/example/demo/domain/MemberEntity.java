package com.example.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;
  private String name;

  private String email;

  private String role;

  private MemberEntity(String username, String name, String email, String role) {
    this.username = username;
    this.name = name;
    this.email = email;
    this.role = role;
  }

  public void updateInfo(String name, String email) {
    this.name = name;
    this.email = email;
  }

  public static MemberEntity create(String username, String name, String email, String role) {
    return new MemberEntity(username, name, email, role);
  }
}
