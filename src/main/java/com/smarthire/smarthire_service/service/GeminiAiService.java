package com.smarthire.smarthire_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class GeminiAiService {

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    public GeminiAiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String analyzeResume(String resumeText, String jobDescription) {
        log.info("Calling Gemini AI to analyze resume...");

        String prompt = buildPrompt(resumeText, jobDescription);

        String requestBody = """
                {
                    "contents": [{
                        "parts": [{
                            "text": "%s"
                        }]
                    }]
                }
                """.formatted(prompt.replace("\"", "'").replace("\n", " "));

        try {
            String response = webClient.post()
                    .uri(GEMINI_URL + "?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Gemini AI response received successfully");
            log.info("Raw Gemini response: {}", response);
            return extractTextFromResponse(response);

        } catch (Exception e) {
            log.error("Error calling Gemini AI: {}", e.getMessage());
            return buildFallbackResponse();
        }
    }

    private String buildPrompt(String resumeText, String jobDescription) {
        return String.format("""
                You are an expert HR recruiter and ATS system analyzer.
                Analyze the following resume against the job description and provide:

                1. MATCH_SCORE: A score from 0-100 indicating how well the resume matches
                2. MISSING_KEYWORDS: Key skills/keywords from the job description missing in resume
                3. SKILLS_GAP: What skills the candidate needs to develop
                4. SUGGESTIONS: Specific suggestions to improve the resume for this role
                5. ATS_COMPATIBILITY: Rate as EXCELLENT/GOOD/AVERAGE/POOR

                Format your response EXACTLY like this:
                MATCH_SCORE: [number]
                MISSING_KEYWORDS: [comma separated list]
                SKILLS_GAP: [description]
                SUGGESTIONS: [specific suggestions]
                ATS_COMPATIBILITY: [rating]

                RESUME:
                %s

                JOB DESCRIPTION:
                %s
                """, resumeText, jobDescription);
    }

    private String extractTextFromResponse(String response) {
        try {
            int textStart = response.indexOf("\"text\": \"") + 9;
            int textEnd = response.lastIndexOf("\"");
            if (textStart > 9 && textEnd > textStart) {
                return response.substring(textStart, textEnd)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"");
            }
        } catch (Exception e) {
            log.error("Error extracting text from Gemini response: {}", e.getMessage());
        }
        return buildFallbackResponse();
    }

    private String buildFallbackResponse() {
        return """
                MATCH_SCORE: 50
                MISSING_KEYWORDS: Unable to analyze at this time
                SKILLS_GAP: Please try again later
                SUGGESTIONS: Ensure your resume matches the job description keywords
                ATS_COMPATIBILITY: AVERAGE
                """;
    }
}