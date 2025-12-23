package com.Smartresumeranker.resumebot.dto;

//get userinput in this variable data format --> email, password
public record SignInRequest(String email, String password) {}