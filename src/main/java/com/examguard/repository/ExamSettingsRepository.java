package com.examguard.repository;

import com.examguard.entity.ExamSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamSettingsRepository extends JpaRepository<ExamSettings, Long> {
    Optional<ExamSettings> findByExamId(Long examId);
}

