package com.examguard.repository;

import com.examguard.entity.AnswerEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerEvaluationRepository extends JpaRepository<AnswerEvaluation, Long> {
    List<AnswerEvaluation> findByStudentAnswerId(Long studentAnswerId);
}

