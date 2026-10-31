package com.examguard.entity;

import com.examguard.entity.enums.Enums.EvaluationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_answers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"attempt_id", "question_id"})
})
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attempt_id", nullable = false)
    private ExamAttempt attempt;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // Comma-separated option IDs for MCQ/multi-select
    @Column(length = 255)
    private String selectedOptionIds;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String textAnswer;

    @Column(nullable = false)
    private boolean isMarkedForReview = false;

    @Column(precision = 5, scale = 2)
    private BigDecimal autoEvaluatedMarks;

    @Column(precision = 5, scale = 2)
    private BigDecimal manualEvaluatedMarks;

    @Column(precision = 5, scale = 2)
    private BigDecimal finalMarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EvaluationStatus evaluationStatus = EvaluationStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String professorComments;

    @Column(nullable = false)
    private LocalDateTime lastSavedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.lastSavedAt = LocalDateTime.now();
    }

    public StudentAnswer() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ExamAttempt getAttempt() { return attempt; }
    public void setAttempt(ExamAttempt attempt) { this.attempt = attempt; }
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    public String getSelectedOptionIds() { return selectedOptionIds; }
    public void setSelectedOptionIds(String selectedOptionIds) { this.selectedOptionIds = selectedOptionIds; }
    public String getTextAnswer() { return textAnswer; }
    public void setTextAnswer(String textAnswer) { this.textAnswer = textAnswer; }
    public boolean isMarkedForReview() { return isMarkedForReview; }
    public void setMarkedForReview(boolean markedForReview) { isMarkedForReview = markedForReview; }
    public BigDecimal getAutoEvaluatedMarks() { return autoEvaluatedMarks; }
    public void setAutoEvaluatedMarks(BigDecimal autoEvaluatedMarks) { this.autoEvaluatedMarks = autoEvaluatedMarks; }
    public BigDecimal getManualEvaluatedMarks() { return manualEvaluatedMarks; }
    public void setManualEvaluatedMarks(BigDecimal manualEvaluatedMarks) { this.manualEvaluatedMarks = manualEvaluatedMarks; }
    public BigDecimal getFinalMarks() { return finalMarks; }
    public void setFinalMarks(BigDecimal finalMarks) { this.finalMarks = finalMarks; }
    public EvaluationStatus getEvaluationStatus() { return evaluationStatus; }
    public void setEvaluationStatus(EvaluationStatus evaluationStatus) { this.evaluationStatus = evaluationStatus; }
    public String getProfessorComments() { return professorComments; }
    public void setProfessorComments(String professorComments) { this.professorComments = professorComments; }
    public LocalDateTime getLastSavedAt() { return lastSavedAt; }
    public void setLastSavedAt(LocalDateTime lastSavedAt) { this.lastSavedAt = lastSavedAt; }
}

