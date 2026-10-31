package com.examguard.dto;

import com.examguard.entity.enums.Enums.LateEntryStatus;
import java.time.LocalDateTime;

public class FeedbackAndLateEntryDTOs {

    public static class SubmitFeedbackRequest {
        private Long examId;
        private int difficultyRating = 3;
        private int clarityRating = 4;
        private int timeSufficiencyRating = 4;
        private int platformRating = 5;
        private boolean technicalIssuesReported = false;
        private String suggestions;

        public Long getExamId() { return examId; }
        public void setExamId(Long examId) { this.examId = examId; }
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
    }

    public static class FeedbackSummaryDTO {
        private double averageDifficulty;
        private double averageClarity;
        private double averageTimeSufficiency;
        private double averagePlatformRating;
        private long totalSubmissions;
        private long technicalIssueReports;

        public double getAverageDifficulty() { return averageDifficulty; }
        public void setAverageDifficulty(double averageDifficulty) { this.averageDifficulty = averageDifficulty; }
        public double getAverageClarity() { return averageClarity; }
        public void setAverageClarity(double averageClarity) { this.averageClarity = averageClarity; }
        public double getAverageTimeSufficiency() { return averageTimeSufficiency; }
        public void setAverageTimeSufficiency(double averageTimeSufficiency) { this.averageTimeSufficiency = averageTimeSufficiency; }
        public double getAveragePlatformRating() { return averagePlatformRating; }
        public void setAveragePlatformRating(double averagePlatformRating) { this.averagePlatformRating = averagePlatformRating; }
        public long getTotalSubmissions() { return totalSubmissions; }
        public void setTotalSubmissions(long totalSubmissions) { this.totalSubmissions = totalSubmissions; }
        public long getTechnicalIssueReports() { return technicalIssueReports; }
        public void setTechnicalIssueReports(long technicalIssueReports) { this.technicalIssueReports = technicalIssueReports; }
    }

    public static class LateEntryRequestDTO {
        private Long id;
        private Long examId;
        private String examTitle;
        private Long studentId;
        private String studentName;
        private String registerNumber;
        private String reason;
        private LateEntryStatus status;
        private String decisionReason;
        private LocalDateTime requestedAt;
        private LocalDateTime validUntil;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getExamId() { return examId; }
        public void setExamId(Long examId) { this.examId = examId; }
        public String getExamTitle() { return examTitle; }
        public void setExamTitle(String examTitle) { this.examTitle = examTitle; }
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getRegisterNumber() { return registerNumber; }
        public void setRegisterNumber(String registerNumber) { this.registerNumber = registerNumber; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public LateEntryStatus getStatus() { return status; }
        public void setStatus(LateEntryStatus status) { this.status = status; }
        public String getDecisionReason() { return decisionReason; }
        public void setDecisionReason(String decisionReason) { this.decisionReason = decisionReason; }
        public LocalDateTime getRequestedAt() { return requestedAt; }
        public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
        public LocalDateTime getValidUntil() { return validUntil; }
        public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }
    }

    public static class LateEntryDecisionRequest {
        private boolean approved;
        private String decisionReason;
        private int validForMinutes = 30;

        public boolean isApproved() { return approved; }
        public void setApproved(boolean approved) { this.approved = approved; }
        public String getDecisionReason() { return decisionReason; }
        public void setDecisionReason(String decisionReason) { this.decisionReason = decisionReason; }
        public int getValidForMinutes() { return validForMinutes; }
        public void setValidForMinutes(int validForMinutes) { this.validForMinutes = validForMinutes; }
    }
}

