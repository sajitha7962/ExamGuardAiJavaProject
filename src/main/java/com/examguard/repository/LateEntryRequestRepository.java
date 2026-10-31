package com.examguard.repository;

import com.examguard.entity.LateEntryRequest;
import com.examguard.entity.enums.Enums.LateEntryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LateEntryRequestRepository extends JpaRepository<LateEntryRequest, Long> {
    List<LateEntryRequest> findByExamIdAndStatus(Long examId, LateEntryStatus status);
    List<LateEntryRequest> findByExamId(Long examId);
    Optional<LateEntryRequest> findByExamIdAndStudentId(Long examId, Long studentId);
}

