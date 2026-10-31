package com.examguard.repository;

import com.examguard.entity.Exam;
import com.examguard.entity.enums.Enums.ExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByProfessorId(Long professorId);
    List<Exam> findByStatus(ExamStatus status);
    List<Exam> findByDepartmentId(Long departmentId);
    long countByStatus(ExamStatus status);

    @Query("SELECT e FROM Exam e WHERE e.status IN ('PUBLISHED', 'ONGOING', 'SCHEDULED') AND " +
           "(e.id IN (SELECT ee.exam.id FROM ExamEligibility ee WHERE ee.department.id = :deptId AND (ee.targetYear IS NULL OR ee.targetYear = :year) AND (ee.targetSection IS NULL OR ee.targetSection = :section) AND ee.isExcluded = false) " +
           "OR e.id IN (SELECT ee.exam.id FROM ExamEligibility ee WHERE ee.student.id = :studentId AND ee.isExcluded = false))")
    List<Exam> findAvailableExamsForStudent(@Param("deptId") Long deptId, @Param("year") int year, @Param("section") String section, @Param("studentId") Long studentId);
}

