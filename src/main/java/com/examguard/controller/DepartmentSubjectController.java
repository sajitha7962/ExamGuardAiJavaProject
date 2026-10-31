package com.examguard.controller;

import com.examguard.dto.ApiResponse;
import com.examguard.entity.Department;
import com.examguard.entity.Subject;
import com.examguard.service.DepartmentSubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class DepartmentSubjectController {

    private final DepartmentSubjectService deptSubService;

    public DepartmentSubjectController(DepartmentSubjectService deptSubService) {
        this.deptSubService = deptSubService;
    }

    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<Department>>> getDepartments() {
        return ResponseEntity.ok(ApiResponse.success(deptSubService.getAllDepartments()));
    }

    @GetMapping("/departments/{deptId}/subjects")
    public ResponseEntity<ApiResponse<List<Subject>>> getSubjectsByDepartment(@PathVariable Long deptId) {
        return ResponseEntity.ok(ApiResponse.success(deptSubService.getSubjectsByDepartment(deptId)));
    }

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<Subject>>> getAllSubjects() {
        return ResponseEntity.ok(ApiResponse.success(deptSubService.getAllSubjects()));
    }
}

