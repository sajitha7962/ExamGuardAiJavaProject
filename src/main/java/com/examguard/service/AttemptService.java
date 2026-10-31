package com.examguard.service;

import com.examguard.dto.AttemptDTOs.*;
import com.examguard.entity.*;
import com.examguard.entity.enums.Enums.AttemptStatus;
import com.examguard.entity.enums.Enums.SubmissionType;
import com.examguard.exception.BadRequestException;
import com.examguard.exception.ResourceNotFoundException;
import com.examguard.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttemptService {

    private final ExamAttemptRepository attemptRepository;
    private final StudentAnswerRepository answerRepository;
    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final StudentRepository studentRepository;
    private final StudentFaceRepository studentFaceRepository;
    private final EvaluationService evaluationService;
    private final AuditLogService auditLogService;

    public AttemptService(ExamAttemptRepository attemptRepository, StudentAnswerRepository answerRepository,
                          ExamRepository examRepository, ExamQuestionRepository examQuestionRepository,
                          StudentRepository studentRepository, StudentFaceRepository studentFaceRepository,
                          EvaluationService evaluationService, AuditLogService auditLogService) {
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.examRepository = examRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.studentRepository = studentRepository;
        this.studentFaceRepository = studentFaceRepository;
        this.evaluationService = evaluationService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public StartAttemptResponse startOrResumeAttempt(Long studentUserId, Long examId, String ipAddress, String browserInfo) {
        Student student = studentRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (exam.getSettings() != null && exam.getSettings().isFaceVerificationEnabled()) {
            if (!studentFaceRepository.existsByStudentId(student.getId())) {
                throw new BadRequestException("Biometric face registration required before starting this examination. Please complete face enrollment first.");
            }
        }

        // Check if an attempt already exists
        Optional<ExamAttempt> existingOpt = attemptRepository.findByExamIdAndStudentId(examId, student.getId());
        ExamAttempt attempt;

        if (existingOpt.isPresent()) {
            attempt = existingOpt.get();
            if (attempt.getStatus() == AttemptStatus.SUBMITTED || attempt.getStatus() == AttemptStatus.EVALUATED) {
                throw new BadRequestException("You have already submitted this examination.");
            }
            if (attempt.getStatus() == AttemptStatus.TERMINATED) {
                throw new BadRequestException("Your examination attempt was terminated due to policy violations.");
            }
            // Check if server deadline passed while away
            if (LocalDateTime.now().isAfter(attempt.getServerDeadline())) {
                SubmitAttemptRequest autoReq = new SubmitAttemptRequest();
                autoReq.setAttemptId(attempt.getId());
                autoReq.setSubmissionType(SubmissionType.AUTO_EXPIRED);
                submitAttempt(studentUserId, autoReq);
                throw new BadRequestException("Examination deadline has expired. Your answers have been auto-submitted.");
            }
        } else {
            // New Attempt
            attempt = new ExamAttempt();
            attempt.setExam(exam);
            attempt.setStudent(student);
            attempt.setAttemptNumber(1);
            attempt.setStartTime(LocalDateTime.now());
            attempt.setServerDeadline(LocalDateTime.now().plusMinutes(exam.getDurationMinutes()));
            attempt.setStatus(AttemptStatus.IN_PROGRESS);
            attempt.setIpAddress(ipAddress);
            attempt.setBrowserInfo(browserInfo);
            attempt = attemptRepository.save(attempt);

            auditLogService.log(student.getUser(), "EXAM_ATTEMPT_STARTED", "EXAM_ATTEMPT", attempt.getId(),
                    "Started attempt for exam: " + exam.getTitle(), ipAddress);
        }

        // Fetch exam questions & construct secure student question view
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamIdOrderByQuestionOrderAsc(examId);
        List<StudentAnswer> savedAnswers = answerRepository.findByAttemptId(attempt.getId());
        Map<Long, StudentAnswer> answerMap = savedAnswers.stream()
                .collect(Collectors.toMap(sa -> sa.getQuestion().getId(), sa -> sa));

        List<ExamRoomQuestionDTO> questionDTOs = new ArrayList<>();
        for (ExamQuestion eq : examQuestions) {
            Question q = eq.getQuestion();
            ExamRoomQuestionDTO qDto = new ExamRoomQuestionDTO();
            qDto.setQuestionId(q.getId());
            qDto.setQuestionOrder(eq.getQuestionOrder());
            qDto.setSectionName(eq.getSectionName());
            qDto.setQuestionText(q.getQuestionText());
            qDto.setQuestionType(q.getQuestionType());
            qDto.setMarks(eq.getCustomMarks() != null ? eq.getCustomMarks() : q.getMarks());
            qDto.setNegativeMarks(q.getNegativeMarks());
            qDto.setImageUrl(q.getImageUrl());

            // Map options WITHOUT correct flag (never leak correct answer to frontend!)
            if (q.getOptions() != null) {
                List<ExamRoomOptionDTO> optList = q.getOptions().stream()
                        .map(o -> new ExamRoomOptionDTO(o.getId(), o.getOptionLetter(), o.getOptionText()))
                        .collect(Collectors.toList());
                qDto.setOptions(optList);
            }

            // Populate saved answer if already exists (for recovery!)
            StudentAnswer sa = answerMap.get(q.getId());
            if (sa != null) {
                qDto.setSavedSelectedOptions(sa.getSelectedOptionIds());
                qDto.setSavedTextAnswer(sa.getTextAnswer());
                qDto.setMarkedForReview(sa.isMarkedForReview());
            }

            questionDTOs.add(qDto);
        }

        // Prepare response
        StartAttemptResponse resp = new StartAttemptResponse();
        resp.setAttemptId(attempt.getId());
        resp.setExamId(exam.getId());
        resp.setExamTitle(exam.getTitle());
        resp.setSubjectName(exam.getSubject().getName() + " (" + exam.getSubject().getCode() + ")");
        resp.setStartTime(attempt.getStartTime());
        resp.setServerDeadline(attempt.getServerDeadline());

        long remainingSec = Math.max(0, Duration.between(LocalDateTime.now(), attempt.getServerDeadline()).getSeconds());
        resp.setRemainingSeconds(remainingSec);

        ExamSettings s = exam.getSettings();
        if (s != null) {
            resp.setAllowBackNav(s.isAllowBackNav());
            resp.setAllowReview(s.isAllowReview());
            resp.setAutoSubmit(s.isAutoSubmit());
            resp.setMaxWarnings(s.getMaxWarnings());
            resp.setFaceVerificationEnabled(s.isFaceVerificationEnabled());
            resp.setContinuousWebcamMonitoring(s.isContinuousWebcamMonitoring());
            resp.setMultipleFaceDetection(s.isMultipleFaceDetection());
            resp.setFaceAbsenceDetection(s.isFaceAbsenceDetection());
            resp.setScreenShareRequired(s.isScreenShareRequired());
            resp.setTabSwitchDetection(s.isTabSwitchDetection());
            resp.setFullscreenMonitoring(s.isFullscreenMonitoring());
        }
        resp.setQuestions(questionDTOs);

        return resp;
    }

    @Transactional
    public void saveAnswer(Long studentUserId, SaveAnswerRequest req) {
        ExamAttempt attempt = attemptRepository.findById(req.getAttemptId())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        if (!attempt.getStudent().getUser().getId().equals(studentUserId)) {
            throw new BadRequestException("Unauthorized access to attempt");
        }

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new BadRequestException("Exam attempt is not in progress. Cannot save answer.");
        }

        if (LocalDateTime.now().isAfter(attempt.getServerDeadline())) {
            throw new BadRequestException("Deadline passed. Cannot save answer.");
        }

        StudentAnswer answer = answerRepository.findByAttemptIdAndQuestionId(req.getAttemptId(), req.getQuestionId())
                .orElse(new StudentAnswer());

        answer.setAttempt(attempt);
        // Find question
        Question question = examQuestionRepository.findByExamIdOrderByQuestionOrderAsc(attempt.getExam().getId())
                .stream().map(ExamQuestion::getQuestion)
                .filter(q -> q.getId().equals(req.getQuestionId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Question not part of this exam"));

        answer.setQuestion(question);
        answer.setSelectedOptionIds(req.getSelectedOptionIds());
        answer.setTextAnswer(req.getTextAnswer());
        answer.setMarkedForReview(req.isMarkedForReview());
        answer.setLastSavedAt(LocalDateTime.now());
        answerRepository.save(answer);
    }

    @Transactional
    public void submitAttempt(Long studentUserId, SubmitAttemptRequest req) {
        ExamAttempt attempt = attemptRepository.findById(req.getAttemptId())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        if (!attempt.getStudent().getUser().getId().equals(studentUserId)) {
            throw new BadRequestException("Unauthorized access to attempt");
        }

        if (attempt.getStatus() == AttemptStatus.SUBMITTED || attempt.getStatus() == AttemptStatus.EVALUATED) {
            return; // Already submitted
        }

        attempt.setStatus(AttemptStatus.SUBMITTED);
        attempt.setEndTime(LocalDateTime.now());
        attempt.setSubmissionType(req.getSubmissionType() != null ? req.getSubmissionType() : SubmissionType.NORMAL);
        attemptRepository.save(attempt);

        auditLogService.log(attempt.getStudent().getUser(), "EXAM_SUBMITTED", "EXAM_ATTEMPT", attempt.getId(),
                "Attempt submitted type: " + attempt.getSubmissionType(), attempt.getIpAddress());

        // Perform automatic evaluation for objective questions
        evaluationService.performAutoEvaluation(attempt);
    }
}
