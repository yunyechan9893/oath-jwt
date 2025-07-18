package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {

  @GetMapping("/")
  public ResponseEntity<String> main() {
    return new ResponseEntity<>("Hello World", HttpStatus.OK);
  }
}
