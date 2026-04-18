package com.smarthire.smarthire_service.controller;

import com.smarthire.smarthire_service.dto.ResumeAnalysisRequest;
import com.smarthire.smarthire_service.dto.ResumeAnalysisResponse;
import com.smarthire.smarthire_service.service.ResumeAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/resume")
@RequiredArgsConstructor
@Slf4j
public class ResumeAnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;

    // Analyze resume from text
    @PostMapping("/analyze")
    public ResponseEntity<ResumeAnalysisResponse> analyzeResume(
            @Valid @RequestBody ResumeAnalysisRequest request) {
        log.info("Received resume analysis request for: {}", request.getCandidateName());
        ResumeAnalysisResponse response = resumeAnalysisService.analyzeResume(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Upload PDF resume and analyze
    @PostMapping("/analyze/pdf")
    public ResponseEntity<ResumeAnalysisResponse> analyzeResumePdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("candidateName") String candidateName,
            @RequestParam("jobDescription") String jobDescription) throws IOException {

        log.info("Received PDF resume for candidate: {}", candidateName);

        // Extract text from PDF
        byte[] bytes = file.getBytes();
        PDDocument document = org.apache.pdfbox.Loader.loadPDF(bytes);
        PDFTextStripper stripper = new PDFTextStripper();
        String resumeText = stripper.getText(document);
        document.close();

        log.info("Extracted {} characters from PDF", resumeText.length());

        ResumeAnalysisRequest request = new ResumeAnalysisRequest();
        request.setCandidateName(candidateName);
        request.setJobDescription(jobDescription);
        request.setResumeText(resumeText);

        ResumeAnalysisResponse response = resumeAnalysisService.analyzeResume(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all analyses for a candidate
    @GetMapping("/candidate/{name}")
    public ResponseEntity<List<ResumeAnalysisResponse>> getByCandidate(
            @PathVariable String name) {
        return ResponseEntity.ok(resumeAnalysisService.getAnalysisByCandidate(name));
    }

    // Get top matching resumes
    @GetMapping("/top-matches")
    public ResponseEntity<List<ResumeAnalysisResponse>> getTopMatches(
            @RequestParam(defaultValue = "70") Integer minScore) {
        return ResponseEntity.ok(resumeAnalysisService.getTopMatches(minScore));
    }
}