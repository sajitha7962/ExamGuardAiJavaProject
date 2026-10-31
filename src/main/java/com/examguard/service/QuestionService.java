package com.examguard.service;

import com.examguard.dto.QuestionDTOs.*;
import com.examguard.entity.Professor;
import com.examguard.entity.Question;
import com.examguard.entity.QuestionOption;
import com.examguard.entity.Subject;
import com.examguard.exception.BadRequestException;
import com.examguard.exception.ResourceNotFoundException;
import com.examguard.repository.ProfessorRepository;
import com.examguard.repository.QuestionOptionRepository;
import com.examguard.repository.QuestionRepository;
import com.examguard.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;
    private final SubjectRepository subjectRepository;
    private final ProfessorRepository professorRepository;
    private final AuditLogService auditLogService;

    public QuestionService(QuestionRepository questionRepository, QuestionOptionRepository optionRepository,
                           SubjectRepository subjectRepository, ProfessorRepository professorRepository,
                           AuditLogService auditLogService) {
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.subjectRepository = subjectRepository;
        this.professorRepository = professorRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public QuestionResponseDTO createQuestion(Long professorUserId, QuestionCreateDTO dto) {
        Professor prof = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor profile not found"));
        if (prof.getApprovalStatus() != com.examguard.entity.enums.Enums.ApprovalStatus.APPROVED) {
            throw new BadRequestException("Your professor account is pending administrator approval.");
        }
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        Question q = new Question();
        q.setProfessor(prof);
        q.setSubject(subject);
        q.setQuestionText(dto.getQuestionText());
        q.setQuestionType(dto.getQuestionType());
        q.setMarks(dto.getMarks());
        q.setNegativeMarks(dto.getNegativeMarks());
        q.setDifficultyLevel(dto.getDifficultyLevel());
        q.setUnitNumber(dto.getUnitNumber());
        q.setExplanation(dto.getExplanation());
        q.setImageUrl(dto.getImageUrl());

        if (dto.getOptions() != null) {
            for (QuestionOptionDTO optDto : dto.getOptions()) {
                QuestionOption opt = new QuestionOption(optDto.getOptionLetter(), optDto.getOptionText(), optDto.isCorrect());
                q.addOption(opt);
            }
        }

        Question saved = questionRepository.save(q);
        auditLogService.log(prof.getUser(), "QUESTION_CREATED", "QUESTION", saved.getId(), "Question created for " + subject.getCode(), "LOCAL");
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByProfessor(Long professorUserId) {
        Professor prof = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor profile not found"));
        return questionRepository.findByProfessorId(prof.getId()).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsBySubject(Long subjectId) {
        return questionRepository.findBySubjectId(subjectId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteQuestion(Long professorUserId, Long questionId) {
        Professor prof = professorRepository.findByUserId(professorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor profile not found"));
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        if (!q.getProfessor().getId().equals(prof.getId())) {
            throw new BadRequestException("Unauthorized to delete this question");
        }
        questionRepository.delete(q);
        auditLogService.log(prof.getUser(), "QUESTION_DELETED", "QUESTION", questionId, "Question deleted", "LOCAL");
    }

    public QuestionResponseDTO toDTO(Question q) {
        QuestionResponseDTO dto = new QuestionResponseDTO();
        dto.setId(q.getId());
        dto.setSubjectId(q.getSubject().getId());
        dto.setSubjectName(q.getSubject().getName() + " (" + q.getSubject().getCode() + ")");
        dto.setQuestionText(q.getQuestionText());
        dto.setQuestionType(q.getQuestionType());
        dto.setMarks(q.getMarks());
        dto.setNegativeMarks(q.getNegativeMarks());
        dto.setDifficultyLevel(q.getDifficultyLevel());
        dto.setUnitNumber(q.getUnitNumber());
        dto.setExplanation(q.getExplanation());
        dto.setImageUrl(q.getImageUrl());

        if (q.getOptions() != null) {
            List<QuestionOptionDTO> optDtos = q.getOptions().stream().map(o -> {
                QuestionOptionDTO od = new QuestionOptionDTO(o.getOptionLetter(), o.getOptionText(), o.isCorrect());
                od.setId(o.getId());
                return od;
            }).collect(Collectors.toList());
            dto.setOptions(optDtos);
        }
        return dto;
    }
}

