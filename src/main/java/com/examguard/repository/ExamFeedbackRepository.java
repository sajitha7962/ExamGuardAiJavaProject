package com.examguard.repository;

import com.examguard.entity.ExamFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamFeedbackRepository extends JpaRepository<ExamFeedback, Long> {
    List<ExamFeedback> findByExamId(Long examId);
    Optional<ExamFeedback> findByExamIdAndStudentId(Long examId, Long studentId);
}

