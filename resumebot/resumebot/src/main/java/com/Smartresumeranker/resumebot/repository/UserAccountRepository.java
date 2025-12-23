package com.Smartresumeranker.resumebot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Smartresumeranker.resumebot.model.UserAccount;


public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
  // findByEmail finds a user account by email
  Optional<UserAccount> findByEmail(String email);
}