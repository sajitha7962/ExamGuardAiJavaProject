package com.examguard.service;

import com.examguard.dto.ProctoringDTOs.*;
import com.examguard.entity.*;
import com.examguard.entity.enums.Enums.AttemptStatus;
import com.examguard.entity.enums.Enums.ProctoringEventType;
import com.examguard.entity.enums.Enums.ReviewStatus;
import com.examguard.entity.enums.Enums.Severity;
import com.examguard.entity.enums.Enums.SubmissionType;
import com.examguard.exception.BadRequestException;
import com.examguard.exception.ResourceNotFoundException;
import com.examguard.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProctoringService {

    private final ProctoringEventRepository proctoringEventRepository;
    private final ExamAttemptRepository attemptRepository;
    private final ExamRepository examRepository;
    private final ProfessorRepository professorRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public ProctoringService(ProctoringEventRepository proctoringEventRepository,
                             ExamAttemptRepository attemptRepository,
                             ExamRepository examRepository,
                             ProfessorRepository professorRepository,
                             AuditLogService auditLogService,
                             NotificationService notificationService) {
        this.proctoringEventRepository = proctoringEventRepository;
        this.attemptRepository = attemptRepository;
        this.examRepository = examRepository;
        this.professorRepository = professorRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Transactional
    public void recordEvent(Long studentUserId, EventLogRequest req) {
        ExamAttempt attempt = attemptRepository.findById(req.getAttemptId())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        if (!attempt.getStudent().getUser().getId().equals(studentUserId)) {
            throw new BadRequestException("Unauthorized attempt access");
        }

        ProctoringEvent event = new ProctoringEvent();
        event.setAttempt(attempt);
        event.setExam(attempt.getExam());
        event.setStudent(attempt.getStudent());
        event.setEventType(req.getEventType());
        event.setSeverity(req.getSeverity() != null ? req.getSeverity() : Severity.WARNING);
        event.setDescription(req.getDescription());
        event.setTimestamp(LocalDateTime.now());
        event.setReviewStatus(ReviewStatus.UNREVIEWED);
        proctoringEventRepository.save(event);

        // Update warnings count if warning severity
        if (event.getSeverity() == Severity.WARNING || event.getSeverity() == Severity.HIGH) {
            attempt.setWarningCount(attempt.getWarningCount() + 1);
            attemptRepository.save(attempt);

            ExamSettings s = attempt.getExam().getSettings();
            if (s != null && s.isAutoSubmitOnViolations() && attempt.getWarningCount() >= s.getMaxWarnings()) {
                attempt.setStatus(AttemptStatus.TERMINATED);
                attempt.setSubmissionType(SubmissionType.VIOLATION_TERMINATED);
                attempt.setEndTime(LocalDateTime.now());
                attemptRepository.save(attempt);

                auditLogService.log(attempt.getStudent().getUser(), "ATTEMPT_TERMINATED_VIOLATION", "EXAM_ATTEMPT",
                        attempt.getId(), "Terminated automatically after " + attempt.getWarningCount() + " warnings.", attempt.getIpAddress());
            }
        }
    }

    @Transactional(readOnly = true)
    public List<StudentLiveStatusDTO> getLiveExamMonitoring(Long professorUserId, Long examId) {
        Professor prof = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor profile not found"));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getProfessor().getId().equals(prof.getId())) {
            throw new BadRequestException("Unauthorized access to live exam monitoring");
        }

        List<ExamAttempt> attempts = attemptRepository.findByExamId(examId);
        List<StudentLiveStatusDTO> statuses = new ArrayList<>();

        for (ExamAttempt att : attempts) {
            Student s = att.getStudent();
            StudentLiveStatusDTO dto = new StudentLiveStatusDTO();
            dto.setAttemptId(att.getId());
            dto.setStudentId(s.getId());
            dto.setStudentName(s.getUser().getFullName());
            dto.setRegisterNumber(s.getRegisterNumber());
            dto.setDepartmentName(s.getDepartment().getName());
            dto.setCurrentYear(s.getCurrentYear());
            dto.setSection(s.getSection());
            dto.setProfileImageUrl(s.getProfileImageUrl());
            dto.setWarningCount(att.getWarningCount());
            dto.setPaused(att.isPaused());
            dto.setStatus(att.getStatus().name());

            long remSec = Math.max(0, Duration.between(LocalDateTime.now(), att.getServerDeadline()).getSeconds());
            dto.setRemainingSeconds(remSec);

            // Calculate risk score based on recorded events
            long highCount = proctoringEventRepository.countByAttemptIdAndSeverity(att.getId(), Severity.HIGH);
            long warnCount = proctoringEventRepository.countByAttemptIdAndSeverity(att.getId(), Severity.WARNING);
            int riskScore = Math.min(100, (int) (highCount * 30 + warnCount * 10));
            dto.setRiskScore(riskScore);

            // Simulation of live camera/face state
            dto.setCameraActive(att.getStatus() == AttemptStatus.IN_PROGRESS);
            dto.setFaceDetected(att.getStatus() == AttemptStatus.IN_PROGRESS);
            dto.setMultipleFacesDetected(false);
            dto.setScreenShareActive(att.getStatus() == AttemptStatus.IN_PROGRESS);
            dto.setLastActivity(LocalDateTime.now());

            statuses.add(dto);
        }

        return statuses;
    }

    @Transactional
    public void executeProfessorAction(Long professorUserId, Long attemptId, ProfessorActionRequest req) {
        Professor prof = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor profile not found"));
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        if (!attempt.getExam().getProfessor().getId().equals(prof.getId())) {
            throw new BadRequestException("Unauthorized to modify student exam session");
        }

        String action = req.getAction().toUpperCase();
        if ("WARNING".equals(action)) {
            attempt.setWarningCount(attempt.getWarningCount() + 1);
            attemptRepository.save(attempt);

            ProctoringEvent event = new ProctoringEvent();
            event.setAttempt(attempt);
            event.setExam(attempt.getExam());
            event.setStudent(attempt.getStudent());
            event.setEventType(ProctoringEventType.WARNING_ISSUED);
            event.setSeverity(Severity.WARNING);
            event.setDescription("Professor issued warning: " + req.getMessage());
            proctoringEventRepository.save(event);

            notificationService.send(attempt.getStudent().getUser().getId(), "Proctoring Warning Issued",
                    req.getMessage(), "PROCTORING_WARNING", "/exam-room.html");

        } else if ("PAUSE".equals(action)) {
            attempt.setPaused(true);
            attemptRepository.save(attempt);

        } else if ("RESUME".equals(action)) {
            attempt.setPaused(false);
            attemptRepository.save(attempt);

        } else if ("EXTEND_TIME".equals(action)) {
            int mins = req.getAdditionalMinutes() > 0 ? req.getAdditionalMinutes() : 10;
            attempt.setAdditionalTimeMinutes(attempt.getAdditionalTimeMinutes() + mins);
            attempt.setServerDeadline(attempt.getServerDeadline().plusMinutes(mins));
            attemptRepository.save(attempt);

            auditLogService.log(prof.getUser(), "EXAM_TIME_EXTENDED", "EXAM_ATTEMPT", attempt.getId(),
                    "Extended time by " + mins + " minutes for " + attempt.getStudent().getRegisterNumber(), "LOCAL");

        } else if ("TERMINATE".equals(action)) {
            attempt.setStatus(AttemptStatus.TERMINATED);
            attempt.setSubmissionType(SubmissionType.PROFESSOR_TERMINATED);
            attempt.setEndTime(LocalDateTime.now());
            attemptRepository.save(attempt);

            auditLogService.log(prof.getUser(), "ATTEMPT_TERMINATED_PROFESSOR", "EXAM_ATTEMPT", attempt.getId(),
                    "Terminated by professor: " + req.getMessage(), "LOCAL");
        }
    }

    @Transactional(readOnly = true)
    public List<ProctoringEvent> getExamEvents(Long professorUserId, Long examId) {
        Professor prof = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor profile not found"));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getProfessor().getId().equals(prof.getId())) {
            throw new BadRequestException("Unauthorized access to exam events");
        }

        return proctoringEventRepository.findByExamIdOrderByTimestampDesc(examId);
    }

    @Transactional
    public void reviewEvent(Long professorUserId, Long eventId, ReviewStatus status, String notes) {
        Professor prof = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor profile not found"));
        ProctoringEvent ev = proctoringEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!ev.getExam().getProfessor().getId().equals(prof.getId())) {
            throw new BadRequestException("Unauthorized to review this event");
        }

        ev.setReviewStatus(status);
        ev.setProfessorNotes(notes);
        proctoringEventRepository.save(ev);
    }
}

