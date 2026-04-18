package com.smarthire.smarthire_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResumeAnalysisRequest {

    @NotBlank(message = "Candidate name is required")
    private String candidateName;

    @NotBlank(message = "Job description is required")
    private String jobDescription;

    // Resume text extracted from PDF
    private String resumeText;
}