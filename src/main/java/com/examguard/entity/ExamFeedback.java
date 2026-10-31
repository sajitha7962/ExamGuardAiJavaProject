package com.examguard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exam_feedback", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"exam_id", "student_id"})
})
public class ExamFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private int difficultyRating = 3; // 1 to 5

    @Column(nullable = false)
    private int clarityRating = 4; // 1 to 5

    @Column(nullable = false)
    private int timeSufficiencyRating = 4; // 1 to 5

    @Column(nullable = false)
    private int platformRating = 5; // 1 to 5

    @Column(nullable = false)
    private boolean technicalIssuesReported = false;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    public ExamFeedback() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public int getDifficultyRating() { return difficultyRating; }
    public void setDifficultyRating(int difficultyRating) { this.difficultyRating = difficultyRating; }
    public int getClarityRating() { return clarityRating; }
    public void setClarityRating(int clarityRating) { this.clarityRating = clarityRating; }
    public int getTimeSufficiencyRating() { return timeSufficiencyRating; }
    public void setTimeSufficiencyRating(int timeSufficiencyRating) { this.timeSufficiencyRating = timeSufficiencyRating; }
    public int getPlatformRating() { return platformRating; }
    public void setPlatformRating(int platformRating) { this.platformRating = platformRating; }
    public boolean isTechnicalIssuesReported() { return technicalIssuesReported; }
    public void setTechnicalIssuesReported(boolean technicalIssuesReported) { this.technicalIssuesReported = technicalIssuesReported; }
    public String getSuggestions() { return suggestions; }
    public void setSuggestions(String suggestions) { this.suggestions = suggestions; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}

