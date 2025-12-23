package com.Smartresumeranker.resumebot.dto;

//get userinput in this format --> fullName, email, password, confirmPassword
public record SignUpRequest(String fullName, String email, String password, String confirmPassword) {}