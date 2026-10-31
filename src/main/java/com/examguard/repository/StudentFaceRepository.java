package com.examguard.repository;

import com.examguard.entity.StudentFace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentFaceRepository extends JpaRepository<StudentFace, Long> {
    Optional<StudentFace> findByStudentId(Long studentId);
    boolean existsByStudentId(Long studentId);
}

