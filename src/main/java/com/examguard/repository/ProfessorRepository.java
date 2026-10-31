package com.examguard.repository;

import com.examguard.entity.Professor;
import com.examguard.entity.enums.Enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByUserId(Long userId);
    Optional<Professor> findByUserEmail(String email);
    Optional<Professor> findByEmployeeId(String employeeId);
    List<Professor> findByApprovalStatus(ApprovalStatus status);
    List<Professor> findByDepartmentId(Long departmentId);
    long countByApprovalStatus(ApprovalStatus status);
}

