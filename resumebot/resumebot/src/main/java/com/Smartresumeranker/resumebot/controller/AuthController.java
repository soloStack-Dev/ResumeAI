package com.Smartresumeranker.resumebot.controller;

import com.Smartresumeranker.resumebot.dto.AuthResponse;
import com.Smartresumeranker.resumebot.dto.SignInRequest;
import com.Smartresumeranker.resumebot.dto.SignUpRequest;
import com.Smartresumeranker.resumebot.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class AuthController {
  // auth is the authentication service
  private final AuthService auth;

  public AuthController(AuthService auth) { this.auth = auth; }

  // signup signs up a new user account
  @PostMapping("/auth/signup")
  public ResponseEntity<AuthResponse> signup(@RequestBody SignUpRequest req) {
    return ResponseEntity.ok(auth.signUp(req));
  }

  // signin signs in a user account
  @PostMapping("/auth/signin")
  public ResponseEntity<AuthResponse> signin(@RequestBody SignInRequest req) {
    return ResponseEntity.ok(auth.signIn(req));
  }
}