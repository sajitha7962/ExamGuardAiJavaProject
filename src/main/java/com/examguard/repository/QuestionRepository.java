package com.examguard.repository;

import com.examguard.entity.Question;
import com.examguard.entity.enums.Enums.DifficultyLevel;
import com.examguard.entity.enums.Enums.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySubjectId(Long subjectId);
    List<Question> findByProfessorId(Long professorId);
    List<Question> findBySubjectIdAndQuestionType(Long subjectId, QuestionType questionType);
    List<Question> findBySubjectIdAndDifficultyLevel(Long subjectId, DifficultyLevel difficulty);
}

