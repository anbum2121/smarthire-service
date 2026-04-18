package com.smarthire.smarthire_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResumeAnalysisResponse {

    private Long id;
    private String candidateName;
    private Integer matchScore;
    private String missingKeywords;
    private String skillsGap;
    private String suggestions;
    private String atsCompatibility;
    private LocalDateTime analyzedAt;
}