package com.examguard.repository;

import com.examguard.entity.ExamAttempt;
import com.examguard.entity.enums.Enums.AttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    Optional<ExamAttempt> findByExamIdAndStudentId(Long examId, Long studentId);
    List<ExamAttempt> findByExamId(Long examId);
    List<ExamAttempt> findByStudentId(Long studentId);
    List<ExamAttempt> findByExamIdAndStatus(Long examId, AttemptStatus status);
    long countByExamId(Long examId);
    long countByExamIdAndStatus(Long examId, AttemptStatus status);
}

