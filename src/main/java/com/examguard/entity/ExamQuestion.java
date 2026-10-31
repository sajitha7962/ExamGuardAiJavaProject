package com.examguard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "exam_questions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"exam_id", "question_id"})
})
public class ExamQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(length = 100)
    private String sectionName = "Section A";

    @Column(nullable = false)
    private int questionOrder = 1;

    @Column(precision = 5, scale = 2)
    private BigDecimal customMarks;

    public ExamQuestion() {}

    public ExamQuestion(Exam exam, Question question, String sectionName, int questionOrder, BigDecimal customMarks) {
        this.exam = exam;
        this.question = question;
        this.sectionName = sectionName;
        this.questionOrder = questionOrder;
        this.customMarks = customMarks;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }
    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }
    public int getQuestionOrder() { return questionOrder; }
    public void setQuestionOrder(int questionOrder) { this.questionOrder = questionOrder; }
    public BigDecimal getCustomMarks() { return customMarks; }
    public void setCustomMarks(BigDecimal customMarks) { this.customMarks = customMarks; }
}

