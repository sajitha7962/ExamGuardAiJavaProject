package com.examguard.service;

import com.examguard.dto.ExamDTOs.*;
import com.examguard.entity.*;
import com.examguard.entity.enums.Enums.ApprovalStatus;
import com.examguard.entity.enums.Enums.ExamStatus;
import com.examguard.exception.BadRequestException;
import com.examguard.exception.ResourceNotFoundException;
import com.examguard.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamSettingsRepository settingsRepository;
    private final ExamEligibilityRepository eligibilityRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionRepository questionRepository;
    private final ProfessorRepository professorRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;
    private final ExamAttemptRepository attemptRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public ExamService(ExamRepository examRepository, ExamSettingsRepository settingsRepository,
                       ExamEligibilityRepository eligibilityRepository, ExamQuestionRepository examQuestionRepository,
                       QuestionRepository questionRepository, ProfessorRepository professorRepository,
                       SubjectRepository subjectRepository, DepartmentRepository departmentRepository,
                       StudentRepository studentRepository, ExamAttemptRepository attemptRepository,
                       AuditLogService auditLogService, NotificationService notificationService) {
        this.examRepository = examRepository;
        this.settingsRepository = settingsRepository;
        this.eligibilityRepository = eligibilityRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.questionRepository = questionRepository;
        this.professorRepository = professorRepository;
        this.subjectRepository = subjectRepository;
        this.departmentRepository = departmentRepository;
        this.studentRepository = studentRepository;
        this.attemptRepository = attemptRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Transactional
    public Exam createExam(Long professorUserId, CreateExamRequest req) {
        Professor prof = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor profile not found"));

        if (prof.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new BadRequestException("Your professor account is pending administrator approval. You cannot publish or create exams yet.");
        }

        Subject subject = subjectRepository.findById(req.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Department dept = departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Exam exam = new Exam();
        exam.setProfessor(prof);
        exam.setSubject(subject);
        exam.setDepartment(dept);
        exam.setTitle(req.getTitle());
        exam.setDescription(req.getDescription());
        exam.setInstructions(req.getInstructions());
        exam.setExamDate(req.getExamDate());
        exam.setStartTime(req.getStartTime());
        exam.setEndTime(req.getEndTime());
        exam.setDurationMinutes(req.getDurationMinutes());
        exam.setTotalMarks(req.getTotalMarks() != null ? req.getTotalMarks() : BigDecimal.valueOf(100.00));
        exam.setPassingMarks(req.getPassingMarks() != null ? req.getPassingMarks() : BigDecimal.valueOf(40.00));
        exam.setStatus(req.getStatus() != null ? req.getStatus() : ExamStatus.DRAFT);

        // Settings & Proctoring
        ExamSettings settings = new ExamSettings();
        if (req.getSettings() != null) {
            ExamSettingsDTO s = req.getSettings();
            settings.setAllowBackNav(s.isAllowBackNav());
            settings.setAllowReview(s.isAllowReview());
            settings.setAutoSubmit(s.isAutoSubmit());
            settings.setEntryWindowMinutes(s.getEntryWindowMinutes());
            settings.setAllowLateEntry(s.isAllowLateEntry());
            settings.setLateEntryMaxMinutes(s.getLateEntryMaxMinutes());
            settings.setMaxAttempts(s.getMaxAttempts());
            settings.setShuffleQuestions(s.isShuffleQuestions());
            settings.setShuffleOptions(s.isShuffleOptions());
            settings.setNegativeMarkingPerQuestion(s.getNegativeMarkingPerQuestion());
            settings.setFaceVerificationEnabled(s.isFaceVerificationEnabled());
            settings.setContinuousWebcamMonitoring(s.isContinuousWebcamMonitoring());
            settings.setMultipleFaceDetection(s.isMultipleFaceDetection());
            settings.setFaceAbsenceDetection(s.isFaceAbsenceDetection());
            settings.setScreenShareRequired(s.isScreenShareRequired());
            settings.setScreenRecordingPermitted(s.isScreenRecordingPermitted());
            settings.setTabSwitchDetection(s.isTabSwitchDetection());
            settings.setWindowFocusDetection(s.isWindowFocusDetection());
            settings.setFullscreenMonitoring(s.isFullscreenMonitoring());
            settings.setMicrophoneMonitoring(s.isMicrophoneMonitoring());
            settings.setPeriodicIdentityCheck(s.isPeriodicIdentityCheck());
            settings.setMaxWarnings(s.getMaxWarnings());
            settings.setAutoSubmitOnViolations(s.isAutoSubmitOnViolations());
        }
        exam.setSettings(settings);
        Exam savedExam = examRepository.save(exam);

        // Eligibility
        if (req.getEligibilityList() != null && !req.getEligibilityList().isEmpty()) {
            for (EligibilityDTO eligDto : req.getEligibilityList()) {
                ExamEligibility ee = new ExamEligibility();
                ee.setExam(savedExam);
                if (eligDto.getDepartmentId() != null) {
                    departmentRepository.findById(eligDto.getDepartmentId()).ifPresent(ee::setDepartment);
                }
                ee.setTargetYear(eligDto.getTargetYear());
                ee.setTargetSection(eligDto.getTargetSection());
                if (eligDto.getStudentId() != null) {
                    studentRepository.findById(eligDto.getStudentId()).ifPresent(ee::setStudent);
                }
                ee.setExcluded(eligDto.isExcluded());
                eligibilityRepository.save(ee);
            }
        } else {
            // Default eligibility to all students in exam's department
            ExamEligibility ee = new ExamEligibility();
            ee.setExam(savedExam);
            ee.setDepartment(dept);
            eligibilityRepository.save(ee);
        }

        // Questions
        if (req.getQuestionIds() != null && !req.getQuestionIds().isEmpty()) {
            int order = 1;
            for (Long qId : req.getQuestionIds()) {
                Question q = questionRepository.findById(qId).orElse(null);
                if (q != null) {
                    ExamQuestion eq = new ExamQuestion(savedExam, q, "Section A", order++, q.getMarks());
                    examQuestionRepository.save(eq);
                }
            }
        }

        auditLogService.log(prof.getUser(), "EXAM_CREATED", "EXAM", savedExam.getId(), "Exam created: " + savedExam.getTitle(), "LOCAL");

        // If published, notify eligible students
        if (savedExam.getStatus() == ExamStatus.PUBLISHED) {
            notifyEligibleStudents(savedExam);
        }

        return savedExam;
    }

    @Transactional
    public void publishExam(Long professorUserId, Long examId) {
        Professor prof = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor profile not found"));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getProfessor().getId().equals(prof.getId())) {
            throw new BadRequestException("Unauthorized to modify this examination");
        }

        exam.setStatus(ExamStatus.PUBLISHED);
        examRepository.save(exam);

        auditLogService.log(prof.getUser(), "EXAM_PUBLISHED", "EXAM", exam.getId(), "Exam published: " + exam.getTitle(), "LOCAL");
        notifyEligibleStudents(exam);
    }

    private void notifyEligibleStudents(Exam exam) {
        List<Student> students = studentRepository.findByDepartmentId(exam.getDepartment().getId());
        for (Student s : students) {
            notificationService.send(s.getUser().getId(), "New Examination Published: " + exam.getTitle(),
                    "Scheduled on " + exam.getExamDate() + " at " + exam.getStartTime() + ". Ensure system readiness & face registration.",
                    "EXAM_SCHEDULED", "/available-exams.html");
        }
    }

    @Transactional(readOnly = true)
    public List<ExamSummaryDTO> getProfessorExams(Long professorUserId) {
        Professor prof = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor profile not found"));

        return examRepository.findByProfessorId(prof.getId()).stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExamSummaryDTO> getAvailableExamsForStudent(Long studentUserId) {
        Student student = studentRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        List<Exam> exams = examRepository.findAvailableExamsForStudent(
                student.getDepartment().getId(), student.getCurrentYear(), student.getSection(), student.getId()
        );

        return exams.stream().map(e -> {
            ExamSummaryDTO dto = toSummaryDTO(e);
            // Check student attempt status
            attemptRepository.findByExamIdAndStudentId(e.getId(), student.getId()).ifPresent(att -> {
                dto.setAttemptStatus(att.getStatus().name());
            });

            // Timing checks
            LocalDate today = LocalDate.now();
            LocalTime nowTime = LocalTime.now();
            boolean isExamDay = e.getExamDate().isEqual(today);

            int entryWindow = e.getSettings() != null ? e.getSettings().getEntryWindowMinutes() : 15;
            LocalTime windowCloseTime = e.getStartTime().plusMinutes(entryWindow);

            boolean withinWindow = isExamDay && (!nowTime.isBefore(e.getStartTime())) && nowTime.isBefore(windowCloseTime);
            boolean lateWindow = isExamDay && (!nowTime.isBefore(windowCloseTime)) && nowTime.isBefore(e.getEndTime());

            dto.setCanStart(withinWindow && (dto.getAttemptStatus() == null || "IN_PROGRESS".equals(dto.getAttemptStatus())));
            dto.setRequiresLateEntry(lateWindow && dto.getAttemptStatus() == null);

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Exam getExamById(Long examId) {
        return examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));
    }

    public ExamSummaryDTO toSummaryDTO(Exam e) {
        ExamSummaryDTO dto = new ExamSummaryDTO();
        dto.setId(e.getId());
        dto.setTitle(e.getTitle());
        dto.setSubjectCode(e.getSubject().getCode());
        dto.setSubjectName(e.getSubject().getName());
        dto.setDepartmentName(e.getDepartment().getName());
        dto.setProfessorName(e.getProfessor().getUser().getFullName());
        dto.setExamDate(e.getExamDate());
        dto.setStartTime(e.getStartTime());
        dto.setEndTime(e.getEndTime());
        dto.setDurationMinutes(e.getDurationMinutes());
        dto.setTotalMarks(e.getTotalMarks());
        dto.setPassingMarks(e.getPassingMarks());
        dto.setStatus(e.getStatus());
        dto.setQuestionCount((int) examQuestionRepository.countByExamId(e.getId()));
        return dto;
    }
}

