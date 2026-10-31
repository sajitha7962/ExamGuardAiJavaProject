package com.examguard.repository;

import com.examguard.entity.ExamEligibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamEligibilityRepository extends JpaRepository<ExamEligibility, Long> {
    List<ExamEligibility> findByExamId(Long examId);
    void deleteByExamId(Long examId);
}

