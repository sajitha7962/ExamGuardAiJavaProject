package com.examguard.entity;

import com.examguard.entity.enums.Enums.PassFailStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_results")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "attempt_id", nullable = false, unique = true)
    private ExamAttempt attempt;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal totalMarksObtained;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal maximumMarks;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(nullable = false, length = 5)
    private String grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PassFailStatus passFailStatus;

    private Integer classRank;

    @Column(nullable = false)
    private boolean isPublished = false;

    private LocalDateTime publishedAt;

    @Column(columnDefinition = "TEXT")
    private String professorRemarks;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ExamResult() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public ExamAttempt getAttempt() { return attempt; }
    public void setAttempt(ExamAttempt attempt) { this.attempt = attempt; }
    public BigDecimal getTotalMarksObtained() { return totalMarksObtained; }
    public void setTotalMarksObtained(BigDecimal totalMarksObtained) { this.totalMarksObtained = totalMarksObtained; }
    public BigDecimal getMaximumMarks() { return maximumMarks; }
    public void setMaximumMarks(BigDecimal maximumMarks) { this.maximumMarks = maximumMarks; }
    public BigDecimal getPercentage() { return percentage; }
    public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public PassFailStatus getPassFailStatus() { return passFailStatus; }
    public void setPassFailStatus(PassFailStatus passFailStatus) { this.passFailStatus = passFailStatus; }
    public Integer getClassRank() { return classRank; }
    public void setClassRank(Integer classRank) { this.classRank = classRank; }
    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public String getProfessorRemarks() { return professorRemarks; }
    public void setProfessorRemarks(String professorRemarks) { this.professorRemarks = professorRemarks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
