package com.Smartresumeranker.resumebot.service;

import com.Smartresumeranker.resumebot.dto.*;
import java.time.Instant;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  // repo is the user account repository
  private final com.Smartresumeranker.resumebot.repository.UserAccountRepository repo;
  // encoder is the password encoder
  private final PasswordEncoder encoder;
  // jwtEncoder is the JWT encoder
  private final JwtEncoder jwtEncoder;

  // constructor injects the user account repository, password encoder, and JWT encoder
  public AuthService(com.Smartresumeranker.resumebot.repository.UserAccountRepository repo, PasswordEncoder encoder, JwtEncoder jwtEncoder) {
    this.repo = repo;
    this.encoder = encoder;
    this.jwtEncoder = jwtEncoder;
  }

  // signUp signs up a new user account
  @Transactional
  public AuthResponse signUp(SignUpRequest req) {
    // validate the signup request
    if (req.email() == null || req.password() == null || !req.password().equals(req.confirmPassword())) {
      // if the email or password is null, or the password and confirm password do not match, throw an exception
      throw new IllegalArgumentException("Invalid signup data");
    }
    // check if the email is already registered
    Optional<com.Smartresumeranker.resumebot.model.UserAccount> existing = repo.findByEmail(req.email().toLowerCase());
    // if the email is already registered, throw an exception
    if (existing.isPresent()) throw new IllegalStateException("Email already registered");
    // create a new user account
    com.Smartresumeranker.resumebot.model.UserAccount ua = new com.Smartresumeranker.resumebot.model.UserAccount();
    // set the email and password hash of the user account
    ua.setEmail(req.email().toLowerCase());
    ua.setPasswordHash(encoder.encode(req.password()));
    // save the user account to the repository
    repo.save(ua);
    // generate a JWT token for the user account
    return new AuthResponse(generateToken(ua));

  }

  // signIn signs in a user account
  public AuthResponse signIn(SignInRequest req) {
    // find the user account by email, or throw an exception if not found
    com.Smartresumeranker.resumebot.model.UserAccount ua = repo.findByEmail(req.email().toLowerCase()).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    // check if the password matches the password hash of the user account, or throw an exception if not
    if (!encoder.matches(req.password(), ua.getPasswordHash())) throw new IllegalArgumentException("Invalid credentials");
    return new AuthResponse(generateToken(ua));
  }

  // generateToken generates a JWT token for a user account
  private String generateToken(com.Smartresumeranker.resumebot.model.UserAccount ua) {
    // create a JWT claims set with the user account email, issued at time, expiration time, and roles
    JwtClaimsSet claims = JwtClaimsSet.builder()
        .subject(ua.getEmail())
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(60L * 60L * 8L))
        .claim("roles", ua.getRoles())
        .build();
    // create a JWT header with the HS256 algorithm
    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
    // encode the JWT claims set with the header and return the token value 
    return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
  }
}
