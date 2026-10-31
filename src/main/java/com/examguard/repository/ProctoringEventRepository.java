package com.examguard.repository;

import com.examguard.entity.ProctoringEvent;
import com.examguard.entity.enums.Enums.ReviewStatus;
import com.examguard.entity.enums.Enums.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProctoringEventRepository extends JpaRepository<ProctoringEvent, Long> {
    List<ProctoringEvent> findByAttemptIdOrderByTimestampDesc(Long attemptId);
    List<ProctoringEvent> findByExamIdOrderByTimestampDesc(Long examId);
    List<ProctoringEvent> findByExamIdAndReviewStatus(Long examId, ReviewStatus status);
    long countByExamId(Long examId);
    long countByAttemptIdAndSeverity(Long attemptId, Severity severity);
}

