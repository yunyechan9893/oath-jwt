package com.example.demo.repository;

import com.example.demo.domain.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

  MemberEntity findByUsername(String username);
}
