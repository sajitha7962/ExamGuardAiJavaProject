package com.examguard.controller;

import com.examguard.dto.AnalyticsDTOs.AdminAnalyticsDTO;
import com.examguard.dto.ApiResponse;
import com.examguard.entity.*;
import com.examguard.entity.enums.Enums.ApprovalStatus;
import com.examguard.exception.BadRequestException;
import com.examguard.exception.ResourceNotFoundException;
import com.examguard.repository.*;
import com.examguard.security.UserPrincipal;
import com.examguard.service.AnalyticsService;
import com.examguard.service.AuditLogService;
import com.examguard.service.DepartmentSubjectService;
import com.examguard.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AnalyticsService analyticsService;
    private final ProfessorRepository professorRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final DepartmentSubjectService deptSubService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public AdminController(AnalyticsService analyticsService, ProfessorRepository professorRepository,
                           StudentRepository studentRepository, UserRepository userRepository,
                           DepartmentSubjectService deptSubService, AuditLogService auditLogService,
                           NotificationService notificationService) {
        this.analyticsService = analyticsService;
        this.professorRepository = professorRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.deptSubService = deptSubService;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminAnalyticsDTO>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getAdminAnalytics()));
    }

    @GetMapping("/professors")
    public ResponseEntity<ApiResponse<List<Professor>>> getProfessors(@RequestParam(required = false) ApprovalStatus status) {
        if (status != null) {
            return ResponseEntity.ok(ApiResponse.success(professorRepository.findByApprovalStatus(status)));
        }
        return ResponseEntity.ok(ApiResponse.success(professorRepository.findAll()));
    }

    @PostMapping("/professors/{id}/decide")
    public ResponseEntity<ApiResponse<Void>> decideProfessor(
            @AuthenticationPrincipal UserPrincipal adminUser,
            @PathVariable Long id,
            @RequestParam boolean approved) {

        Professor prof = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found"));
        User admin = userRepository.findById(adminUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        prof.setApprovalStatus(approved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED);
        prof.setApprovedByAdmin(admin);
        prof.setApprovalDate(LocalDateTime.now());
        professorRepository.save(prof);

        auditLogService.log(admin, "PROFESSOR_APPROVAL_DECISION", "PROFESSOR", prof.getId(),
                "Professor " + prof.getUser().getFullName() + " status updated to " + prof.getApprovalStatus(), "LOCAL");

        String msg = approved
                ? "Congratulations! Your ExamGuard AI professor account has been approved by admin. You can now create and manage exams."
                : "Your professor registration was not approved. Please contact university admin.";
        notificationService.send(prof.getUser().getId(), "Professor Account Status", msg, "ACCOUNT_STATUS", "/login.html");

        return ResponseEntity.ok(ApiResponse.success("Professor decision recorded successfully.", null));
    }

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<Student>>> getStudents() {
        return ResponseEntity.ok(ApiResponse.success(studentRepository.findAll()));
    }

    @PostMapping("/users/{userId}/toggle-active")
    public ResponseEntity<ApiResponse<Void>> toggleUserActive(@AuthenticationPrincipal UserPrincipal admin, @PathVariable Long userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        u.setActive(!u.isActive());
        userRepository.save(u);

        auditLogService.log(null, "USER_STATUS_TOGGLE", "USER", u.getId(),
                "User " + u.getEmail() + " active status set to: " + u.isActive(), "LOCAL");

        return ResponseEntity.ok(ApiResponse.success("User status updated", null));
    }

    @PostMapping("/departments")
    public ResponseEntity<ApiResponse<Department>> createDepartment(@RequestBody Department dept) {
        return ResponseEntity.ok(ApiResponse.success("Department created", deptSubService.createDepartment(dept)));
    }

    @PostMapping("/departments/{deptId}/subjects")
    public ResponseEntity<ApiResponse<Subject>> createSubject(@PathVariable Long deptId, @RequestBody Subject sub) {
        return ResponseEntity.ok(ApiResponse.success("Subject created", deptSubService.createSubject(deptId, sub)));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs() {
        return ResponseEntity.ok(ApiResponse.success(auditLogService.getRecentLogs()));
    }
}

