package com.smarthire.smarthire_service.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String candidateName;

    @Column(nullable = false, length = 5000)
    private String jobDescription;

    @Column(length = 10000)
    private String resumeText;

    @Column(nullable = false)
    private Integer matchScore;

    @Column(length = 5000)
    private String missingKeywords;

    @Column(length = 5000)
    private String skillsGap;

    @Column(length = 5000)
    private String suggestions;

    @Column(nullable = false)
    private String atsCompatibility;

    @Column(nullable = false)
    private LocalDateTime analyzedAt;
}