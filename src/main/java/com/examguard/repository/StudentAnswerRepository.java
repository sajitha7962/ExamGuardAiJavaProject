package com.examguard.repository;

import com.examguard.entity.StudentAnswer;
import com.examguard.entity.enums.Enums.EvaluationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findByAttemptId(Long attemptId);
    Optional<StudentAnswer> findByAttemptIdAndQuestionId(Long attemptId, Long questionId);
    List<StudentAnswer> findByAttemptIdAndEvaluationStatus(Long attemptId, EvaluationStatus status);
}

