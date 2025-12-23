package com.Smartresumeranker.resumebot.model;

import java.time.Instant;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserAccount {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String passwordHash;

  // createdAt is final, it can be set only once, when the user account is created
  private final Instant createdAt = Instant.now();

  // roles is final, it can be set only once, when the user account is created
  @ElementCollection(fetch = FetchType.EAGER)
  // roles is a set of strings, each string is a role name
  private Set<String> roles = java.util.Set.of("USER");

  public Long getId() { return id; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
  public Instant getCreatedAt() { return createdAt; }
  public Set<String> getRoles() { return roles; }
  public void setRoles(Set<String> roles) { this.roles = roles; }
}