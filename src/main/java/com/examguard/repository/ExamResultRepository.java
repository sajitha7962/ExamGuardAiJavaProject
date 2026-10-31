package com.examguard.repository;

import com.examguard.entity.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    Optional<ExamResult> findByAttemptId(Long attemptId);
    Optional<ExamResult> findByExamIdAndStudentId(Long examId, Long studentId);
    List<ExamResult> findByExamId(Long examId);
    List<ExamResult> findByStudentId(Long studentId);
    List<ExamResult> findByExamIdOrderByTotalMarksObtainedDesc(Long examId);
}

