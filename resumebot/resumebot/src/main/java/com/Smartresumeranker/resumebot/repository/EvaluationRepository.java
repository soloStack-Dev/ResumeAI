package com.Smartresumeranker.resumebot.repository;

import com.Smartresumeranker.resumebot.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {}