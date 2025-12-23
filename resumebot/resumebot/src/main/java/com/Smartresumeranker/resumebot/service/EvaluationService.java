package com.Smartresumeranker.resumebot.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.Smartresumeranker.resumebot.dto.EvaluationResponse;
import com.Smartresumeranker.resumebot.model.Evaluation;
import com.Smartresumeranker.resumebot.repository.EvaluationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

// EvaluationService is the service layer for the Evaluation entity
@Service
public class EvaluationService {
  // ChatClient is the client for the AI model
  private final ChatClient chatClient;
  // EvaluationRepository is the repository for the Evaluation entity
  private final EvaluationRepository repository;

  // EvaluationService constructor
  public EvaluationService(ChatClient.Builder chatClientBuilder, EvaluationRepository repository) {
    // ChatClient is the client for the AI model
    this.chatClient = chatClientBuilder.build();
    // EvaluationRepository is the repository for the Evaluation entity
    this.repository = repository;
  }

  // evaluate is the method to evaluate the resume
  @Transactional
  public EvaluationResponse evaluate(MultipartFile resumeFile, String resumeText, String jobDescription) {
    // resume is the text of the resume
    String resume = StringUtils.hasText(resumeText) ? resumeText : extractText(resumeFile);
    // jd is the job description
    String jd = jobDescription == null ? "" : jobDescription;

    Map<String, Object> ai = generateInsights(resume, jd);

    Evaluation entity = new Evaluation();
    entity.setResumeText(resume);
    entity.setJobDescription(jd);
    entity.setMissingSkills((List<String>) ai.getOrDefault("missingSkills", List.of()));
    entity.setSuggestions((List<String>) ai.getOrDefault("suggestions", List.of()));
    entity.setRewrittenBullets((List<String>) ai.getOrDefault("rewrittenBullets", List.of()));
    repository.save(entity);

    return new EvaluationResponse(
        entity.getId(),
        entity.getMissingSkills(),
        entity.getSuggestions(),
        entity.getRewrittenBullets(),
        entity.getResumeText(),
        entity.getJobDescription());
  }

  private String extractText(MultipartFile file) {
    //if the resume have no file name return nothing response
    if (file == null || file.isEmpty()) return "";
    //But resume have a original name store in original variable
    String original = file.getOriginalFilename();
    //if original have no name return default name resume but it's have name return into lowercase
    String name = (original == null) ? "resume" : original.toLowerCase();

    //if the resume have pdf extension then read the pdf file and extract the text
    try {
      if (name.endsWith(".pdf")) {
        //create a temporary file to store the pdf file
        File tmp = Files.createTempFile("resume-", ".pdf").toFile();
        //transfer the pdf file to the temporary file
        file.transferTo(tmp);
        //read the pdf file and extract the text
        PdfDocumentReaderConfig cfg = PdfDocumentReaderConfig.builder().build();
        //read the pdf file and extract the text
        PagePdfDocumentReader reader = new PagePdfDocumentReader(new FileSystemResource(tmp), cfg);
        StringBuilder sb = new StringBuilder();
        //store the pdf file text in sb variable
        reader.get().forEach(d -> sb.append(d.getText()).append("\n"));
        //delete the temporary file
        tmp.delete();
        //return the pdf file text
        return sb.toString();
      }
      //if the resume have not pdf extension then read the file and extract the text
      return new String(file.getBytes());
    } catch (IOException e) {
      return "";
    }
  }

  //generateInsights is the method to generate the insights from the resume and job description
  //insights are missingSkills, suggestions, rewrittenBullets
  private Map<String, Object> generateInsights(String resume, String jd) {

    //prompt is the prompt to generate the insights from the resume and job description
    String prompt = """
                    You are an ATS resume expert. Given RESUME and JOB DESCRIPTION, return JSON with keys: missingSkills (array), suggestions (array of short strings), rewrittenBullets (array of bullet strings).
                    Identify missing skills based on the job description.
                    Provide actionable suggestions to improve the resume.
                    Rewrite bullet points to be more impactful using action verbs and metrics.
                    Keep responses concise. Return only JSON.
                    RESUME:
                    """ + resume + "\nJOB DESCRIPTION:\n" + jd + "\nReturn only JSON.";
    String content;
    try {
      //call the chat model to generate the insights from the resume and job description
      content = chatClient.prompt().user(prompt).call().content();
    } catch (Exception ex) {
      //if the chat model not available then return the default insights
      java.util.Map<String, Object> out = new java.util.HashMap<>();
      out.put("missingSkills", java.util.List.of());
      out.put("suggestions", java.util.List.of("Chat model not available", "Install or configure Ollama chat model"));
      out.put("rewrittenBullets", java.util.List.of());
      return out;
    }
    try {
      //parse the json response from the chat model
      com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
      //parse the json response from the chat model and store in map variable
      Map<String, Object> map = om.readValue(content, om.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
      //convert the missingSkills, suggestions, rewrittenBullets to list of string
      List<String> ms = toStringList(map.get("missingSkills"));
      //convert the suggestions to list of string
      List<String> sug = toStringList(map.get("suggestions"));
      //convert the rewrittenBullets to list of string
      List<String> bullets = toStringList(map.get("rewrittenBullets"));

      //if the suggestions or rewrittenBullets are empty then generate the fallback suggestions or rewrittenBullets
      if (sug == null || sug.isEmpty()) sug = fallbackSuggestions(ms, jd);
      if (bullets == null || bullets.isEmpty()) bullets = fallbackBullets(ms);

      //return the insights in map variable
      //missingSkills, suggestions, rewrittenBullets are the keys in map variable
      Map<String, Object> out = new java.util.HashMap<>();
      out.put("missingSkills", ms);
      out.put("suggestions", sug);
      out.put("rewrittenBullets", bullets);
      return out;
    } catch (JsonProcessingException e) {
      Map<String, Object> out = new java.util.HashMap<>();
      out.put("missingSkills", List.of());
      out.put("suggestions", List.of());
      out.put("rewrittenBullets", List.of());
      return out;
    }
  }

  //fallbackSuggestions is the method to generate the fallback suggestions from the missingSkills and job description
  private List<String> fallbackSuggestions(List<String> skills, String jd) {
    List<String> base = skills.size() > 10 ? skills.subList(0, 10) : skills;
    List<String> out = new java.util.ArrayList<>();
    /*
    it generate default suggestions for the missingSkills
    like a Include sprinboot explicitly in resume and summary
    Add quantified bullet highlighting springboot impact
     */
    for (String s : base) {
      out.add("Include " + s + " explicitly in resume and summary");
      out.add("Add quantified bullet highlighting " + s + " impact");
    }
    /*
    This method generates safe, meaningful resume improvement suggestions when:
      AI returns empty suggestions
      AI output is invalid / incomplete
      You still want to show ATS-friendly advice to the user
      It never returns null and limits noise.
     */

      //when user upload job description is null or length less than 20 retrun this message
      //message: Align keywords and phrasing with job description
    if (jd != null && jd.length() > 20) out.add("Align keywords and phrasing with job description");
    java.util.LinkedHashSet<String> set = new java.util.LinkedHashSet<>(out);
    out.clear();
    out.addAll(set);
    if (out.size() > 10) return out.subList(0, 10);
    return out;
  }

  //fallbackBullets is the method to generate the fallback rewrittenBullets from the missingSkills
  private List<String> fallbackBullets(List<String> skills) {
    /*
    it generate default rewrittenBullets for the missingSkills
    like a Delivered measurable outcomes using springboot, improving KPIs and efficiency
     */
    List<String> base = skills.size() > 6 ? skills.subList(0, 6) : skills;
    List<String> out = new java.util.ArrayList<>();
    for (String s : base) {
      out.add("Delivered measurable outcomes using " + s + ", improving KPIs and efficiency");
    }
    java.util.LinkedHashSet<String> set = new java.util.LinkedHashSet<>(out);
    out.clear();
    out.addAll(set);
    return out;
  }

  //toStringList is the method to convert the object to list of string
  private List<String> toStringList(Object o) {
    //if the object is null then return empty list
    List<String> r = new ArrayList<>();
    
    if (o instanceof List<?> l) {
      for (Object x : l) r.add(String.valueOf(x));
    }
    return r;
  }
}
