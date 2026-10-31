package com.examguard.controller;

import com.examguard.dto.AnalyticsDTOs.ProfessorAnalyticsDTO;
import com.examguard.dto.ApiResponse;
import com.examguard.dto.EvaluationAndResultDTOs.ManualGradingSubmitRequest;
import com.examguard.dto.ExamDTOs.CreateExamRequest;
import com.examguard.dto.ExamDTOs.ExamSummaryDTO;
import com.examguard.dto.FeedbackAndLateEntryDTOs.FeedbackSummaryDTO;
import com.examguard.dto.FeedbackAndLateEntryDTOs.LateEntryDecisionRequest;
import com.examguard.dto.FeedbackAndLateEntryDTOs.LateEntryRequestDTO;
import com.examguard.dto.ProctoringDTOs.ProfessorActionRequest;
import com.examguard.dto.ProctoringDTOs.StudentLiveStatusDTO;
import com.examguard.dto.QuestionDTOs.QuestionCreateDTO;
import com.examguard.dto.QuestionDTOs.QuestionResponseDTO;
import com.examguard.entity.Exam;
import com.examguard.entity.ExamResult;
import com.examguard.entity.Professor;
import com.examguard.entity.Student;
import com.examguard.entity.enums.Enums.ReviewStatus;
import com.examguard.exception.ResourceNotFoundException;
import com.examguard.repository.ExamResultRepository;
import com.examguard.repository.ProfessorRepository;
import com.examguard.repository.StudentRepository;
import com.examguard.security.UserPrincipal;
import com.examguard.service.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professor")
public class ProfessorController {

    private final AnalyticsService analyticsService;
    private final ExamService examService;
    private final QuestionService questionService;
    private final ProctoringService proctoringService;
    private final EvaluationService evaluationService;
    private final LateEntryService lateEntryService;
    private final FeedbackService feedbackService;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final ExamResultRepository resultRepository;

    public ProfessorController(AnalyticsService analyticsService, ExamService examService,
                               QuestionService questionService, ProctoringService proctoringService,
                               EvaluationService evaluationService, LateEntryService lateEntryService,
                               FeedbackService feedbackService, StudentRepository studentRepository,
                               ProfessorRepository professorRepository, ExamResultRepository resultRepository) {
        this.analyticsService = analyticsService;
        this.examService = examService;
        this.questionService = questionService;
        this.proctoringService = proctoringService;
        this.evaluationService = evaluationService;
        this.lateEntryService = lateEntryService;
        this.feedbackService = feedbackService;
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.resultRepository = resultRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<ProfessorAnalyticsDTO>> getDashboard(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getProfessorAnalytics(user.getId())));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Professor>> getProfile(@AuthenticationPrincipal UserPrincipal user) {
        Professor prof = professorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found"));
        return ResponseEntity.ok(ApiResponse.success(prof));
    }

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<Student>>> getMyStudents(@AuthenticationPrincipal UserPrincipal user) {
        Professor prof = professorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Professor not found"));
        return ResponseEntity.ok(ApiResponse.success(studentRepository.findByDepartmentId(prof.getDepartment().getId())));
    }

    @PostMapping("/exams")
    public ResponseEntity<ApiResponse<Exam>> createExam(@AuthenticationPrincipal UserPrincipal user,
                                                        @Valid @RequestBody CreateExamRequest req) {
        Exam exam = examService.createExam(user.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Exam created successfully", exam));
    }

    @GetMapping("/exams")
    public ResponseEntity<ApiResponse<List<ExamSummaryDTO>>> getExams(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(examService.getProfessorExams(user.getId())));
    }

    @GetMapping("/exams/{examId}")
    public ResponseEntity<ApiResponse<Exam>> getExamDetail(@PathVariable Long examId) {
        return ResponseEntity.ok(ApiResponse.success(examService.getExamById(examId)));
    }

    @PostMapping("/exams/{examId}/publish")
    public ResponseEntity<ApiResponse<Void>> publishExam(@AuthenticationPrincipal UserPrincipal user, @PathVariable Long examId) {
        examService.publishExam(user.getId(), examId);
        return ResponseEntity.ok(ApiResponse.success("Exam published successfully", null));
    }

    // Question bank
    @PostMapping("/questions")
    public ResponseEntity<ApiResponse<QuestionResponseDTO>> createQuestion(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody QuestionCreateDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Question created", questionService.createQuestion(user.getId(), dto)));
    }

    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<List<QuestionResponseDTO>>> getQuestions(@AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(questionService.getQuestionsByProfessor(user.getId())));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@AuthenticationPrincipal UserPrincipal user, @PathVariable Long id) {
        questionService.deleteQuestion(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Question deleted", null));
    }

    // Live Monitoring
    @GetMapping("/exams/{examId}/live-monitoring")
    public ResponseEntity<ApiResponse<List<StudentLiveStatusDTO>>> getLiveMonitoring(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long examId) {
        return ResponseEntity.ok(ApiResponse.success(proctoringService.getLiveExamMonitoring(user.getId(), examId)));
    }

    @PostMapping("/attempts/{attemptId}/action")
    public ResponseEntity<ApiResponse<Void>> executeAttemptAction(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long attemptId,
            @RequestBody ProfessorActionRequest req) {
        proctoringService.executeProfessorAction(user.getId(), attemptId, req);
        return ResponseEntity.ok(ApiResponse.success("Action executed successfully", null));
    }

    @GetMapping("/exams/{examId}/events")
    public ResponseEntity<ApiResponse<List<?>>> getExamEvents(@AuthenticationPrincipal UserPrincipal user, @PathVariable Long examId) {
        return ResponseEntity.ok(ApiResponse.success(proctoringService.getExamEvents(user.getId(), examId)));
    }

    @PostMapping("/events/{eventId}/review")
    public ResponseEntity<ApiResponse<Void>> reviewEvent(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long eventId,
            @RequestParam ReviewStatus status,
            @RequestParam(required = false) String notes) {
        proctoringService.reviewEvent(user.getId(), eventId, status, notes);
        return ResponseEntity.ok(ApiResponse.success("Event review recorded", null));
    }

    // Evaluation & Results
    @PostMapping("/manual-grading")
    public ResponseEntity<ApiResponse<Void>> submitManualGrading(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody ManualGradingSubmitRequest req) {
        evaluationService.submitManualGrading(user.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Manual evaluation submitted successfully", null));
    }

    @PostMapping("/exams/{examId}/publish-results")
    public ResponseEntity<ApiResponse<Void>> publishResults(@AuthenticationPrincipal UserPrincipal user, @PathVariable Long examId) {
        evaluationService.publishResultsForExam(user.getId(), examId);
        return ResponseEntity.ok(ApiResponse.success("Examination results published to students.", null));
    }

    @GetMapping("/exams/{examId}/results")
    public ResponseEntity<ApiResponse<List<ExamResult>>> getExamResults(@PathVariable Long examId) {
        return ResponseEntity.ok(ApiResponse.success(resultRepository.findByExamId(examId)));
    }

    // Late Entry Requests
    @GetMapping("/exams/{examId}/late-entries")
    public ResponseEntity<ApiResponse<List<LateEntryRequestDTO>>> getLateEntries(@PathVariable Long examId) {
        return ResponseEntity.ok(ApiResponse.success(lateEntryService.getPendingRequestsForExam(examId)));
    }

    @PostMapping("/late-entries/{requestId}/decide")
    public ResponseEntity<ApiResponse<Void>> decideLateEntry(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long requestId,
            @RequestBody LateEntryDecisionRequest req) {
        lateEntryService.decideLateEntry(user.getId(), requestId, req);
        return ResponseEntity.ok(ApiResponse.success("Decision recorded", null));
    }

    // Feedback
    @GetMapping("/exams/{examId}/feedback")
    public ResponseEntity<ApiResponse<FeedbackSummaryDTO>> getExamFeedback(@PathVariable Long examId) {
        return ResponseEntity.ok(ApiResponse.success(feedbackService.getExamFeedbackSummary(examId)));
    }
}

