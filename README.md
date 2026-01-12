| 📸 Preview |
|-----------|
| ![](https://i.ibb.co/p6Xz8dZ3/Screenshot-2025-12-25-065118.png) |

🚀 AI-Powered Resume & Job Description Analyzer

Secure Full-Stack AI System with Resume Intelligence, Skill Gap Detection & Smart Suggestions

📌 Project Overview

This project is a full-stack AI-powered web application that analyzes a resume and job description to determine job-fit quality, detect missing skills, and generate AI-powered improvement suggestions.
Unlike traditional resume checkers, this system:

  Uses AI reasoning to evaluate alignment
  Provides explainable feedback
  Supports secure authentication
  Stores analysis history
  Runs AI locally using Ollama

🎯 Purpose of the Application
The purpose of this application is to:
  Help users evaluate whether their resume matches a given job description
  Identify missing skills and gaps using AI
  Provide intelligent rewriting suggestions for resume bullet points
  Ensure secure access using modern authentication methods
  Demonstrate real-world AI system design for academic and professional use

🧠 Core Features
  ✅ Resume & Job Description Analysis
    Upload resume content
    Paste job description
    AI compares both documents
  Determines match quality
    📉 If Resume Is Weak
  AI provides:
    Missing Skills
    AI Suggestions
    Rewritten Resume Bullet Points
  ✅ If Resume Is Strong
    AI returns:
      Job compatibility confirmation
      Positive feedback
      Readiness score

🔐 Security & Authentication
JWT Authentication
  Secure backend API access
  Token-based authorization
  OAuth Authentication[Google Login,GitHub Login]
  Protected routes ensure only authenticated users can access AI analysis pages

🗃 Data Storage
  Resume data
  Job description data
  AI analysis results
  User authentication details
  Stored securely in:
    Spring Boot H2 Database


🤖 AI Capabilities
  AI Engine
    Ollama (Local AI Runtime)
    Model: granite3.2-vision:2b

  AI Inputs
    Resume text
    Job description text (Optional) Image-based resume input

  AI Outputs
    Skill gap analysis
    Resume improvement suggestions
    Bullet rewriting
    Match confirmation

  🧱 System Architecture

  Frontend (Vite + React + TS)
        |
        | Axios API Calls (JWT Secured)
        ↓
  Backend (Spring Boot)
        |
        | Prisma-like ORM (Spring JPA)
        ↓
  H2 Database
        |
        ↓
  Ollama AI Engine
  (Granite 3.2 Vision Model)


⚙️ Technology Stack
  🎨 Frontend:
      Vite + React,TypeScript,Tailwind CSS,CSS,React Router,Axios (API integration)

  🛠 Backend:
      Java,Spring Boot,Spring Security,JWT Authentication,OAuth (Google & GitHub),Spring Data JPA,H2 Database

  🤖 AI & Dev Tools:
      Ollama (Local AI Engine)[Granite 3.2 Vision Model],Trae AI Code Assistant IDE,Context-aware AI,Custom Code Agent

  🐳 DevOps:
      Docker,GitHub

## 📄 Project Documentation

[Click here to view the PDF](docs/AI_Resume_Job_Analyzer_Documentation.pdf)


👨‍🎓 Author:
  Faleel
    Final Year Student
    Java Full Stack Developer | AI Enthusiast
      Passionate about building secure, scalable, and AI-driven web applications
      Interested in backend systems, AI integration, and modern software architecture
