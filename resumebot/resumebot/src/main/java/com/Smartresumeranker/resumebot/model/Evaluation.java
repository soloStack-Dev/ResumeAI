package com.Smartresumeranker.resumebot.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Evaluation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Lob
  private String resumeText;

  @Lob
  private String jobDescription;

  @ElementCollection
  private List<String> missingSkills;

  @ElementCollection
  private List<String> suggestions;

  @ElementCollection
  private List<String> rewrittenBullets;

  private Instant createdAt = Instant.now();
}