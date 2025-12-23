package com.Smartresumeranker.resumebot.dto;

import java.util.List;

//ai generate response in this variables of data format to shown in web page --> id, missingSkills, suggestions, rewrittenBullets, resumeText, jobDescription
public record EvaluationResponse(
    // id is the unique identifier for the evaluation
    Long id,
    List<String> missingSkills,
    List<String> suggestions,
    List<String> rewrittenBullets,
    String resumeText,
    String jobDescription) {}