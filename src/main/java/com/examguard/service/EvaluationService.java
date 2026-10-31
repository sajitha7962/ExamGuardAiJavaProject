package com.examguard.service;

import com.examguard.dto.EvaluationAndResultDTOs.*;
import com.examguard.entity.*;
import com.examguard.entity.enums.Enums.AttemptStatus;
import com.examguard.entity.enums.Enums.EvaluationStatus;
import com.examguard.entity.enums.Enums.PassFailStatus;
import com.examguard.entity.enums.Enums.QuestionType;
import com.examguard.exception.BadRequestException;
import com.examguard.exception.ResourceNotFoundException;
import com.examguard.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EvaluationService {

    private final StudentAnswerRepository answerRepository;
    private final ExamAttemptRepository attemptRepository;
    private final ExamResultRepository resultRepository;
    private final AnswerEvaluationRepository evaluationRepository;
    private final ProfessorRepository professorRepository;
    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public EvaluationService(StudentAnswerRepository answerRepository, ExamAttemptRepository attemptRepository,
                             ExamResultRepository resultRepository, AnswerEvaluationRepository evaluationRepository,
                             ProfessorRepository professorRepository, ExamRepository examRepository,
                             StudentRepository studentRepository,
                             AuditLogService auditLogService, NotificationService notificationService) {
        this.answerRepository = answerRepository;
        this.attemptRepository = attemptRepository;
        this.resultRepository = resultRepository;
        this.evaluationRepository = evaluationRepository;
        this.professorRepository = professorRepository;
        this.examRepository = examRepository;
        this.studentRepository = studentRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Transactional
    public void performAutoEvaluation(ExamAttempt attempt) {
        List<StudentAnswer> answers = answerRepository.findByAttemptId(attempt.getId());
        BigDecimal totalObtained = BigDecimal.ZERO;
        boolean hasPendingManual = false;

        for (StudentAnswer sa : answers) {
            Question q = sa.getQuestion();
            QuestionType type = q.getQuestionType();

            if (type == QuestionType.MCQ || type == QuestionType.TRUE_FALSE) {
                String selectedOptIdStr = sa.getSelectedOptionIds();
                boolean correct = false;
                if (selectedOptIdStr != null && !selectedOptIdStr.trim().isEmpty()) {
                    try {
                        Long selectedOptId = Long.parseLong(selectedOptIdStr.trim());
                        correct = q.getOptions().stream()
                                .anyMatch(opt -> opt.getId().equals(selectedOptId) && opt.isCorrect());
                    } catch (NumberFormatException ignored) {}
                }

                if (correct) {
                    sa.setAutoEvaluatedMarks(q.getMarks());
                    sa.setFinalMarks(q.getMarks());
                    totalObtained = totalObtained.add(q.getMarks());
                } else {
                    BigDecimal negative = q.getNegativeMarks() != null ? q.getNegativeMarks() : BigDecimal.ZERO;
                    sa.setAutoEvaluatedMarks(negative.negate());
                    sa.setFinalMarks(negative.negate());
                    totalObtained = totalObtained.subtract(negative);
                }
                sa.setEvaluationStatus(EvaluationStatus.AUTO_EVALUATED);
                answerRepository.save(sa);

            } else if (type == QuestionType.MULTI_SELECT) {
                Set<Long> correctIds = q.getOptions().stream()
                        .filter(QuestionOption::isCorrect)
                        .map(QuestionOption::getId)
                        .collect(Collectors.toSet());

                Set<Long> selectedIds = new HashSet<>();
                if (sa.getSelectedOptionIds() != null && !sa.getSelectedOptionIds().trim().isEmpty()) {
                    for (String part : sa.getSelectedOptionIds().split(",")) {
                        try { selectedIds.add(Long.parseLong(part.trim())); } catch (NumberFormatException ignored) {}
                    }
                }

                if (correctIds.equals(selectedIds)) {
                    sa.setAutoEvaluatedMarks(q.getMarks());
                    sa.setFinalMarks(q.getMarks());
                    totalObtained = totalObtained.add(q.getMarks());
                } else {
                    sa.setAutoEvaluatedMarks(BigDecimal.ZERO);
                    sa.setFinalMarks(BigDecimal.ZERO);
                }
                sa.setEvaluationStatus(EvaluationStatus.AUTO_EVALUATED);
                answerRepository.save(sa);

            } else if (type == QuestionType.FILL_BLANK) {
                // Check if matches any correct option text
                String text = sa.getTextAnswer() != null ? sa.getTextAnswer().trim().toLowerCase() : "";
                boolean match = q.getOptions().stream()
                        .filter(QuestionOption::isCorrect)
                        .anyMatch(opt -> opt.getOptionText().trim().toLowerCase().equals(text));

                if (match) {
                    sa.setAutoEvaluatedMarks(q.getMarks());
                    sa.setFinalMarks(q.getMarks());
                    totalObtained = totalObtained.add(q.getMarks());
                } else {
                    sa.setAutoEvaluatedMarks(BigDecimal.ZERO);
                    sa.setFinalMarks(BigDecimal.ZERO);
                }
                sa.setEvaluationStatus(EvaluationStatus.AUTO_EVALUATED);
                answerRepository.save(sa);

            } else {
                // SHORT_ANSWER or DESCRIPTIVE -> Requires manual grading
                hasPendingManual = true;
                sa.setEvaluationStatus(EvaluationStatus.PENDING);
                answerRepository.save(sa);
            }
        }

        if (totalObtained.compareTo(BigDecimal.ZERO) < 0) {
            totalObtained = BigDecimal.ZERO;
        }

        // Always generate initial result record (with auto-evaluated marks)
        generateResult(attempt, totalObtained, false);

        // If no manual grading needed, finalize status
        if (!hasPendingManual) {
            attempt.setStatus(AttemptStatus.EVALUATED);
            attemptRepository.save(attempt);
        } else {
            // Notify professor of pending manual evaluation
            notificationService.send(attempt.getExam().getProfessor().getUser().getId(),
                    "Manual Evaluation Pending: " + attempt.getExam().getTitle(),
                    "Student " + attempt.getStudent().getUser().getFullName() + " (" + attempt.getStudent().getRegisterNumber() + ") submitted answers requiring review.",
                    "EVALUATION_PENDING", "/manual-evaluation.html?examId=" + attempt.getExam().getId());
        }
    }

    @Transactional
    public void submitManualGrading(Long professorUserId, ManualGradingSubmitRequest req) {
        Professor prof = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor profile not found"));
        ExamAttempt attempt = attemptRepository.findById(req.getAttemptId())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        if (!attempt.getExam().getProfessor().getId().equals(prof.getId())) {
            throw new BadRequestException("Unauthorized to evaluate this examination");
        }

        for (ManualGradingItem item : req.getGradings()) {
            StudentAnswer sa = null;
            if (item.getAnswerId() != null) {
                sa = answerRepository.findById(item.getAnswerId()).orElse(null);
            }
            if (sa == null) {
                sa = answerRepository.findByAttemptId(attempt.getId()).stream()
                        .filter(a -> a.getQuestion().getQuestionType() == QuestionType.DESCRIPTIVE ||
                                     a.getQuestion().getQuestionType() == QuestionType.SHORT_ANSWER ||
                                     a.getEvaluationStatus() == EvaluationStatus.PENDING)
                        .findFirst()
                        .orElse(null);
            }
            if (sa == null) {
                continue;
            }

            sa.setManualEvaluatedMarks(item.getMarksAwarded());
            sa.setFinalMarks(item.getMarksAwarded());
            sa.setProfessorComments(item.getFeedback());
            sa.setEvaluationStatus(EvaluationStatus.MANUALLY_EVALUATED);
            answerRepository.save(sa);

            AnswerEvaluation eval = new AnswerEvaluation();
            eval.setStudentAnswer(sa);
            eval.setProfessor(prof);
            eval.setMarksAwarded(item.getMarksAwarded());
            eval.setFeedback(item.getFeedback());
            evaluationRepository.save(eval);
        }

        // Calculate total marks across all answers
        List<StudentAnswer> allAnswers = answerRepository.findByAttemptId(attempt.getId());
        BigDecimal totalMarks = BigDecimal.ZERO;
        for (StudentAnswer sa : allAnswers) {
            if (sa.getFinalMarks() != null) {
                totalMarks = totalMarks.add(sa.getFinalMarks());
            }
        }
        if (totalMarks.compareTo(BigDecimal.ZERO) < 0) totalMarks = BigDecimal.ZERO;

        attempt.setStatus(AttemptStatus.EVALUATED);
        attemptRepository.save(attempt);

        generateResult(attempt, totalMarks, false);
        auditLogService.log(prof.getUser(), "MANUAL_EVALUATION_COMPLETED", "EXAM_ATTEMPT", attempt.getId(),
                "Completed manual grading for attempt: " + attempt.getId(), "LOCAL");
    }

    @Transactional
    public void generateResult(ExamAttempt attempt, BigDecimal totalMarksObtained, boolean publishImmediately) {
        Exam exam = attempt.getExam();
        ExamResult result = resultRepository.findByAttemptId(attempt.getId()).orElse(new ExamResult());

        result.setExam(exam);
        result.setStudent(attempt.getStudent());
        result.setAttempt(attempt);
        result.setTotalMarksObtained(totalMarksObtained);
        result.setMaximumMarks(exam.getTotalMarks());

        BigDecimal percentage = totalMarksObtained.divide(exam.getTotalMarks(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        result.setPercentage(percentage);

        result.setPassFailStatus(totalMarksObtained.compareTo(exam.getPassingMarks()) >= 0 ? PassFailStatus.PASS : PassFailStatus.FAIL);

        // Grade calculation
        if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) result.setGrade("O");
        else if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) result.setGrade("A+");
        else if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) result.setGrade("A");
        else if (percentage.compareTo(BigDecimal.valueOf(60)) >= 0) result.setGrade("B+");
        else if (percentage.compareTo(BigDecimal.valueOf(50)) >= 0) result.setGrade("B");
        else if (percentage.compareTo(BigDecimal.valueOf(40)) >= 0) result.setGrade("C");
        else result.setGrade("F");

        result.setPublished(publishImmediately);
        if (publishImmediately) {
            result.setPublishedAt(LocalDateTime.now());
        }

        resultRepository.save(result);
    }

    @Transactional
    public void publishResultsForExam(Long professorUserId, Long examId) {
        Professor prof = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor profile not found"));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getProfessor().getId().equals(prof.getId())) {
            throw new BadRequestException("Unauthorized to publish results for this exam");
        }

        List<ExamResult> results = resultRepository.findByExamIdOrderByTotalMarksObtainedDesc(examId);
        int rank = 1;
        for (ExamResult r : results) {
            r.setClassRank(rank++);
            r.setPublished(true);
            r.setPublishedAt(LocalDateTime.now());
            resultRepository.save(r);

            // Notify student
            notificationService.send(r.getStudent().getUser().getId(),
                    "Examination Results Published: " + exam.getTitle(),
                    "Your marks: " + r.getTotalMarksObtained() + "/" + r.getMaximumMarks() + " (Grade: " + r.getGrade() + ")",
                    "RESULTS_PUBLISHED", "/student-results.html?examId=" + examId);
        }

        auditLogService.log(prof.getUser(), "RESULTS_PUBLISHED", "EXAM", examId,
                "Published results for " + results.size() + " students", "LOCAL");
    }

    @Transactional(readOnly = true)
    public StudentResultDetailDTO getStudentResult(Long studentUserId, Long examId) {
        Student student = studentRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        ExamResult res = resultRepository.findByExamIdAndStudentId(examId, student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Result not found for this examination"));

        if (!res.isPublished()) {
            throw new BadRequestException("Results have not yet been published by the professor.");
        }

        StudentResultDetailDTO dto = new StudentResultDetailDTO();
        dto.setResultId(res.getId());
        dto.setExamId(res.getExam().getId());
        dto.setExamTitle(res.getExam().getTitle());
        dto.setSubjectCode(res.getExam().getSubject().getCode());
        dto.setSubjectName(res.getExam().getSubject().getName());
        dto.setDepartmentName(res.getExam().getDepartment().getName());
        dto.setProfessorName(res.getExam().getProfessor().getUser().getFullName());
        dto.setStudentName(student.getUser().getFullName());
        dto.setRegisterNumber(student.getRegisterNumber());
        dto.setTotalMarksObtained(res.getTotalMarksObtained());
        dto.setMaximumMarks(res.getMaximumMarks());
        dto.setPercentage(res.getPercentage());
        dto.setGrade(res.getGrade());
        dto.setPassFailStatus(res.getPassFailStatus().name());
        dto.setClassRank(res.getClassRank());
        dto.setPublished(res.isPublished());
        dto.setPublishedAt(res.getPublishedAt());
        dto.setProfessorRemarks(res.getProfessorRemarks());

        // Question details
        List<StudentAnswer> answers = answerRepository.findByAttemptId(res.getAttempt().getId());
        List<ResultQuestionDetailDTO> qDetails = answers.stream().map(a -> {
            ResultQuestionDetailDTO qd = new ResultQuestionDetailDTO();
            qd.setQuestionId(a.getQuestion().getId());
            qd.setQuestionText(a.getQuestion().getQuestionText());
            qd.setQuestionType(a.getQuestion().getQuestionType().name());
            qd.setMaxMarks(a.getQuestion().getMarks());
            qd.setMarksObtained(a.getFinalMarks() != null ? a.getFinalMarks() : BigDecimal.ZERO);
            qd.setStudentAnswer(a.getTextAnswer() != null ? a.getTextAnswer() : a.getSelectedOptionIds());
            qd.setExplanation(a.getQuestion().getExplanation());
            qd.setProfessorComment(a.getProfessorComments());

            // Correct answer text
            String correctOpts = a.getQuestion().getOptions().stream()
                    .filter(QuestionOption::isCorrect)
                    .map(o -> o.getOptionLetter() + ": " + o.getOptionText())
                    .collect(Collectors.joining("; "));
            qd.setCorrectAnswer(correctOpts);

            return qd;
        }).collect(Collectors.toList());

        dto.setQuestionDetails(qDetails);
        return dto;
    }
}
