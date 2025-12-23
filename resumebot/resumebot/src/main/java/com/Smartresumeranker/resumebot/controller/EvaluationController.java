package com.Smartresumeranker.resumebot.controller;

import com.Smartresumeranker.resumebot.dto.EvaluationResponse;
import com.Smartresumeranker.resumebot.model.Evaluation;
import com.Smartresumeranker.resumebot.repository.EvaluationRepository;
import com.Smartresumeranker.resumebot.service.EvaluationService;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
public class EvaluationController {
  private final EvaluationService service;
  private final EvaluationRepository repository;

  public EvaluationController(EvaluationService service, EvaluationRepository repository) {
    this.service = service;
    this.repository = repository;
  }

  //rank is the method to evaluate the resume and job description
  @PostMapping(path = "/api/rank", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public EvaluationResponse rank(
      @RequestPart(name = "resumeFile", required = false) MultipartFile resumeFile,
      @RequestPart(name = "resumeText", required = false) String resumeText,
      @RequestPart(name = "jobDescription") String jobDescription) {
    return service.evaluate(resumeFile, resumeText, jobDescription);
  }

  @GetMapping("/api/rank")
  public List<Evaluation> list() {
    return repository.findAll();
  }

  @GetMapping("/api/rank/{id}")
  public Evaluation get(@PathVariable Long id) {
    return repository.findById(id).orElse(null);
  }
}
