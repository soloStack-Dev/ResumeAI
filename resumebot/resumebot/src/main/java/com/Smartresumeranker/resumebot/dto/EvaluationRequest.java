package com.Smartresumeranker.resumebot.dto;

import org.springframework.web.multipart.MultipartFile;

//accept user input in this variable format --> resumeFile, resumeText, jobDescription
public record EvaluationRequest(MultipartFile resumeFile, String resumeText, String jobDescription) {}