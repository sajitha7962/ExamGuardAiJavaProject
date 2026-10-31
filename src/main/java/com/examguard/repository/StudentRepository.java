package com.examguard.repository;

import com.examguard.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserId(Long userId);
    Optional<Student> findByUserEmail(String email);
    Optional<Student> findByRegisterNumber(String registerNumber);
    List<Student> findByDepartmentId(Long departmentId);
    List<Student> findByDepartmentIdAndCurrentYearAndSection(Long departmentId, int currentYear, String section);
    long countByDepartmentId(Long departmentId);
}

