package com.examguard.controller;

import com.examguard.dto.AnalyticsDTOs.StudentAnalyticsDTO;
import com.examguard.dto.ApiResponse;
import com.examguard.dto.AttemptDTOs.SaveAnswerRequest;
import com.examguard.dto.AttemptDTOs.StartAttemptResponse;
import com.examguard.dto.AttemptDTOs.SubmitAttemptRequest;
import com.examguard.dto.EvaluationAndResultDTOs.StudentResultDetailDTO;
import com.examguard.dto.ExamDTOs.ExamSummaryDTO;
import com.examguard.dto.FeedbackAndLateEntryDTOs.SubmitFeedbackRequest;
import com.examguard.dto.ProctoringDTOs.EventLogRequest;
import com.examguard.dto.SupportAndNotificationDTOs.CreateTicketRequest;
import com.examguard.entity.Exam;
import com.examguard.entity.Student;
import com.examguard.entity.SupportTicket;
import com.examguard.exception.ResourceNotFoundException;
import com.examguard.repository.StudentRepository;
import com.examguard.security.UserPrincipal;
import com.examguard.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final AnalyticsService analyticsService;
    private final ExamService examService;
    private final AttemptService attemptService;
    private final EvaluationService evaluationService;
    private final ProctoringService proctoringService;
    private final LateEntryService lateEntryService;
    private final FeedbackService feedbackService;
    private final SupportService supportService;
    private final StudentRepository studentRepository;

    public StudentController(AnalyticsService analyticsService, ExamService examService,
                             AttemptService attemptService, EvaluationService evaluationService,
                             ProctoringService proctoringService, LateEntryService lateEntryService,
                             FeedbackService feedbackService, SupportService supportService,
                             StudentRepository studentRepository) {
        this.analyticsService = analyticsService;
        this.examService = examService;
        this.attemptService = attemptService;
        this.evaluationService = evaluationService;
        this.proctoringService = proctoringService;
        this.lateEntryService = lateEntryService;
        this.feedbackService = feedbackService;
        this.supportService = supportService;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<StudentAnalyticsDTO>> getDashboard(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getStudentAnalytics(user.getId())));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Student>> getProfile(@AuthenticationPrincipal UserPrincipal user) {
        Student s = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return ResponseEntity.ok(ApiResponse.success(s));
    }

    @GetMapping({"/available-exams", "/exams/available"})
    public ResponseEntity<ApiResponse<List<ExamSummaryDTO>>> getAvailableExams(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(examService.getAvailableExamsForStudent(user.getId())));
    }

    @GetMapping("/exams/{examId}")
    public ResponseEntity<ApiResponse<Exam>> getExamDetails(@PathVariable Long examId) {
        return ResponseEntity.ok(ApiResponse.success(examService.getExamById(examId)));
    }

    @PostMapping("/exams/{examId}/start")
    public ResponseEntity<ApiResponse<StartAttemptResponse>> startExam(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long examId,
            HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String browser = request.getHeader("User-Agent");
        StartAttemptResponse resp = attemptService.startOrResumeAttempt(user.getId(), examId, ip, browser);
        return ResponseEntity.ok(ApiResponse.success("Exam attempt active", resp));
    }

    @PostMapping("/exams/save-answer")
    public ResponseEntity<ApiResponse<Void>> saveAnswer(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody SaveAnswerRequest req) {
        attemptService.saveAnswer(user.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Answer saved", null));
    }

    @PostMapping("/exams/submit")
    public ResponseEntity<ApiResponse<Void>> submitExam(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody SubmitAttemptRequest req) {
        attemptService.submitAttempt(user.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Examination submitted successfully.", null));
    }

    @PostMapping("/proctoring/event")
    public ResponseEntity<ApiResponse<Void>> logProctoringEvent(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody EventLogRequest req) {
        proctoringService.recordEvent(user.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Event recorded", null));
    }

    @PostMapping("/exams/{examId}/request-late-entry")
    public ResponseEntity<ApiResponse<Void>> requestLateEntry(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long examId,
            @RequestParam String reason) {
        lateEntryService.requestLateEntry(user.getId(), examId, reason);
        return ResponseEntity.ok(ApiResponse.success("Late entry request submitted to professor.", null));
    }

    @GetMapping("/exams/{examId}/results")
    public ResponseEntity<ApiResponse<StudentResultDetailDTO>> getResult(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long examId) {
        return ResponseEntity.ok(ApiResponse.success(evaluationService.getStudentResult(user.getId(), examId)));
    }

    @PostMapping("/exams/feedback")
    public ResponseEntity<ApiResponse<Void>> submitFeedback(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody SubmitFeedbackRequest req) {
        feedbackService.submitFeedback(user.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Feedback submitted. Thank you!", null));
    }

    @PostMapping("/support/tickets")
    public ResponseEntity<ApiResponse<SupportTicket>> createTicket(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateTicketRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Ticket submitted", supportService.createTicket(user.getId(), req)));
    }

    @GetMapping("/support/tickets")
    public ResponseEntity<ApiResponse<List<SupportTicket>>> getTickets(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(supportService.getUserTickets(user.getId())));
    }
}
