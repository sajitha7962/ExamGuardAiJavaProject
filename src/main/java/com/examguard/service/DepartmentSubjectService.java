package com.examguard.service;

import com.examguard.entity.Department;
import com.examguard.entity.Subject;
import com.examguard.exception.BadRequestException;
import com.examguard.exception.ResourceNotFoundException;
import com.examguard.repository.DepartmentRepository;
import com.examguard.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentSubjectService {

    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;

    public DepartmentSubjectService(DepartmentRepository departmentRepository, SubjectRepository subjectRepository) {
        this.departmentRepository = departmentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Transactional
    public Department createDepartment(Department dept) {
        if (departmentRepository.existsByCode(dept.getCode())) {
            throw new BadRequestException("Department code already exists: " + dept.getCode());
        }
        return departmentRepository.save(dept);
    }

    @Transactional(readOnly = true)
    public List<Subject> getSubjectsByDepartment(Long departmentId) {
        return subjectRepository.findByDepartmentId(departmentId);
    }

    @Transactional(readOnly = true)
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Transactional
    public Subject createSubject(Long deptId, Subject sub) {
        Department dept = departmentRepository.findById(deptId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + deptId));
        sub.setDepartment(dept);
        return subjectRepository.save(sub);
    }
}

