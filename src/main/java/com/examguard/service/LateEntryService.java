package com.examguard.service;

import com.examguard.dto.FeedbackAndLateEntryDTOs.*;
import com.examguard.entity.*;
import com.examguard.entity.enums.Enums.LateEntryStatus;
import com.examguard.exception.BadRequestException;
import com.examguard.exception.ResourceNotFoundException;
import com.examguard.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LateEntryService {

    private final LateEntryRequestRepository lateEntryRepository;
    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public LateEntryService(LateEntryRequestRepository lateEntryRepository, ExamRepository examRepository,
                            StudentRepository studentRepository, ProfessorRepository professorRepository,
                            AuditLogService auditLogService, NotificationService notificationService) {
        this.lateEntryRepository = lateEntryRepository;
        this.examRepository = examRepository;
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Transactional
    public void requestLateEntry(Long studentUserId, Long examId, String reason) {
        Student student = studentRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        LateEntryRequest req = lateEntryRepository.findByExamIdAndStudentId(examId, student.getId())
                .orElse(new LateEntryRequest());

        req.setExam(exam);
        req.setStudent(student);
        req.setReason(reason);
        req.setStatus(LateEntryStatus.PENDING);
        req.setRequestedAt(LocalDateTime.now());
        lateEntryRepository.save(req);

        // Notify professor
        notificationService.send(exam.getProfessor().getUser().getId(),
                "Late Entry Requested: " + student.getUser().getFullName(),
                "Student " + student.getRegisterNumber() + " requested late entry for " + exam.getTitle() + ". Reason: " + reason,
                "LATE_ENTRY_REQUEST", "/live-monitoring.html?examId=" + examId);
    }

    @Transactional
    public void decideLateEntry(Long professorUserId, Long requestId, LateEntryDecisionRequest decision) {
        Professor prof = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor profile not found"));
        LateEntryRequest req = lateEntryRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Late entry request not found"));

        if (!req.getExam().getProfessor().getId().equals(prof.getId())) {
            throw new BadRequestException("Unauthorized to decide this late entry request");
        }

        req.setStatus(decision.isApproved() ? LateEntryStatus.APPROVED : LateEntryStatus.REJECTED);
        req.setDecisionReason(decision.getDecisionReason());
        req.setProfessor(prof);
        req.setDecidedAt(LocalDateTime.now());
        if (decision.isApproved()) {
            req.setValidUntil(LocalDateTime.now().plusMinutes(decision.getValidForMinutes()));
        }
        lateEntryRepository.save(req);

        auditLogService.log(prof.getUser(), "LATE_ENTRY_DECISION", "LATE_ENTRY_REQUEST", req.getId(),
                "Decision: " + req.getStatus() + " for student: " + req.getStudent().getRegisterNumber(), "LOCAL");

        // Notify student
        String msg = decision.isApproved()
                ? "Your late entry request for " + req.getExam().getTitle() + " has been APPROVED. You may enter now."
                : "Your late entry request for " + req.getExam().getTitle() + " was REJECTED: " + decision.getDecisionReason();
        notificationService.send(req.getStudent().getUser().getId(), "Late Entry Decision", msg, "LATE_ENTRY_DECISION", "/available-exams.html");
    }

    @Transactional(readOnly = true)
    public List<LateEntryRequestDTO> getPendingRequestsForExam(Long examId) {
        return lateEntryRepository.findByExamIdAndStatus(examId, LateEntryStatus.PENDING).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private LateEntryRequestDTO toDTO(LateEntryRequest r) {
        LateEntryRequestDTO dto = new LateEntryRequestDTO();
        dto.setId(r.getId());
        dto.setExamId(r.getExam().getId());
        dto.setExamTitle(r.getExam().getTitle());
        dto.setStudentId(r.getStudent().getId());
        dto.setStudentName(r.getStudent().getUser().getFullName());
        dto.setRegisterNumber(r.getStudent().getRegisterNumber());
        dto.setReason(r.getReason());
        dto.setStatus(r.getStatus());
        dto.setDecisionReason(r.getDecisionReason());
        dto.setRequestedAt(r.getRequestedAt());
        dto.setValidUntil(r.getValidUntil());
        return dto;
    }
}

