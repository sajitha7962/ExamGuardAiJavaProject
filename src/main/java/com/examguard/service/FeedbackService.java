package com.examguard.service;

import com.examguard.dto.FeedbackAndLateEntryDTOs.*;
import com.examguard.entity.Exam;
import com.examguard.entity.ExamFeedback;
import com.examguard.entity.Student;
import com.examguard.exception.BadRequestException;
import com.examguard.exception.ResourceNotFoundException;
import com.examguard.repository.ExamFeedbackRepository;
import com.examguard.repository.ExamRepository;
import com.examguard.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeedbackService {

    private final ExamFeedbackRepository feedbackRepository;
    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final AuditLogService auditLogService;

    public FeedbackService(ExamFeedbackRepository feedbackRepository, ExamRepository examRepository,
                           StudentRepository studentRepository, AuditLogService auditLogService) {
        this.feedbackRepository = feedbackRepository;
        this.examRepository = examRepository;
        this.studentRepository = studentRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public void submitFeedback(Long studentUserId, SubmitFeedbackRequest req) {
        Student student = studentRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        Exam exam = examRepository.findById(req.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (feedbackRepository.findByExamIdAndStudentId(req.getExamId(), student.getId()).isPresent()) {
            throw new BadRequestException("You have already submitted feedback for this examination.");
        }

        ExamFeedback fb = new ExamFeedback();
        fb.setExam(exam);
        fb.setStudent(student);
        fb.setDifficultyRating(req.getDifficultyRating());
        fb.setClarityRating(req.getClarityRating());
        fb.setTimeSufficiencyRating(req.getTimeSufficiencyRating());
        fb.setPlatformRating(req.getPlatformRating());
        fb.setTechnicalIssuesReported(req.isTechnicalIssuesReported());
        fb.setSuggestions(req.getSuggestions());
        feedbackRepository.save(fb);

        auditLogService.log(student.getUser(), "EXAM_FEEDBACK_SUBMITTED", "EXAM_FEEDBACK", fb.getId(),
                "Feedback submitted for " + exam.getTitle(), "CLIENT");
    }

    @Transactional(readOnly = true)
    public FeedbackSummaryDTO getExamFeedbackSummary(Long examId) {
        List<ExamFeedback> list = feedbackRepository.findByExamId(examId);
        FeedbackSummaryDTO dto = new FeedbackSummaryDTO();
        dto.setTotalSubmissions(list.size());

        if (list.isEmpty()) {
            dto.setAverageDifficulty(0);
            dto.setAverageClarity(0);
            dto.setAverageTimeSufficiency(0);
            dto.setAveragePlatformRating(0);
            dto.setTechnicalIssueReports(0);
            return dto;
        }

        double diffSum = 0, clarSum = 0, timeSum = 0, platSum = 0;
        long techIssues = 0;

        for (ExamFeedback fb : list) {
            diffSum += fb.getDifficultyRating();
            clarSum += fb.getClarityRating();
            timeSum += fb.getTimeSufficiencyRating();
            platSum += fb.getPlatformRating();
            if (fb.isTechnicalIssuesReported()) techIssues++;
        }

        int count = list.size();
        dto.setAverageDifficulty(Math.round((diffSum / count) * 10.0) / 10.0);
        dto.setAverageClarity(Math.round((clarSum / count) * 10.0) / 10.0);
        dto.setAverageTimeSufficiency(Math.round((timeSum / count) * 10.0) / 10.0);
        dto.setAveragePlatformRating(Math.round((platSum / count) * 10.0) / 10.0);
        dto.setTechnicalIssueReports(techIssues);

        return dto;
    }
}

