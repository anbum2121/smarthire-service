package com.smarthire.smarthire_service.service;

import com.smarthire.smarthire_service.dto.ResumeAnalysisRequest;
import com.smarthire.smarthire_service.dto.ResumeAnalysisResponse;
import com.smarthire.smarthire_service.model.ResumeAnalysis;
import com.smarthire.smarthire_service.repository.ResumeAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeAnalysisService {

    private final ResumeAnalysisRepository repository;
    private final GeminiAiService geminiAiService;

    public ResumeAnalysisResponse analyzeResume(ResumeAnalysisRequest request) {
        log.info("Starting resume analysis for candidate: {}", request.getCandidateName());

        // Call Gemini AI
        String aiResponse = geminiAiService.analyzeResume(
                request.getResumeText(),
                request.getJobDescription()
        );

        // Parse AI response
        int matchScore = parseMatchScore(aiResponse);
        String missingKeywords = parseField(aiResponse, "MISSING_KEYWORDS");
        String skillsGap = parseField(aiResponse, "SKILLS_GAP");
        String suggestions = parseField(aiResponse, "SUGGESTIONS");
        String atsCompatibility = parseField(aiResponse, "ATS_COMPATIBILITY");

        // Save to database
        ResumeAnalysis analysis = ResumeAnalysis.builder()
                .candidateName(request.getCandidateName())
                .jobDescription(request.getJobDescription())
                .resumeText(request.getResumeText())
                .matchScore(matchScore)
                .missingKeywords(missingKeywords)
                .skillsGap(skillsGap)
                .suggestions(suggestions)
                .atsCompatibility(atsCompatibility)
                .analyzedAt(LocalDateTime.now())
                .build();

        ResumeAnalysis saved = repository.save(analysis);
        log.info("Resume analysis saved with id: {}", saved.getId());

        return mapToResponse(saved);
    }

    public List<ResumeAnalysisResponse> getAnalysisByCandidate(String candidateName) {
        return repository.findByCandidateName(candidateName)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ResumeAnalysisResponse> getTopMatches(Integer minScore) {
        return repository.findByMatchScoreGreaterThanEqual(minScore)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private int parseMatchScore(String aiResponse) {
        try {
            String line = aiResponse.lines()
                    .filter(l -> l.startsWith("MATCH_SCORE:"))
                    .findFirst()
                    .orElse("MATCH_SCORE: 50");
            String scoreStr = line.replace("MATCH_SCORE:", "").trim();
            return Integer.parseInt(scoreStr.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            log.warn("Could not parse match score, defaulting to 50");
            return 50;
        }
    }

    private String parseField(String aiResponse, String fieldName) {
        try {
            return aiResponse.lines()
                    .filter(l -> l.startsWith(fieldName + ":"))
                    .findFirst()
                    .map(l -> l.replace(fieldName + ":", "").trim())
                    .orElse("Not available");
        } catch (Exception e) {
            return "Not available";
        }
    }

    private ResumeAnalysisResponse mapToResponse(ResumeAnalysis analysis) {
        return ResumeAnalysisResponse.builder()
                .id(analysis.getId())
                .candidateName(analysis.getCandidateName())
                .matchScore(analysis.getMatchScore())
                .missingKeywords(analysis.getMissingKeywords())
                .skillsGap(analysis.getSkillsGap())
                .suggestions(analysis.getSuggestions())
                .atsCompatibility(analysis.getAtsCompatibility())
                .analyzedAt(analysis.getAnalyzedAt())
                .build();
    }
}