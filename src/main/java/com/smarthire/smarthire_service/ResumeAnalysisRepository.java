package com.smarthire.smarthire_service.repository;
import com.smarthire.smarthire_service.model.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {

    List<ResumeAnalysis> findByCandidateName(String candidateName);

    List<ResumeAnalysis> findByMatchScoreGreaterThanEqual(Integer matchScore);
}