package com.examguard.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AnalyticsDTOs {

    public static class StudentAnalyticsDTO {
        private long totalExams;
        private long completedExams;
        private long upcomingExams;
        private long pendingResults;
        private BigDecimal averageScore;
        private BigDecimal highestScore;
        private List<String> examLabels;
        private List<BigDecimal> examScores;
        private Map<String, BigDecimal> subjectWiseAverage;

        public long getTotalExams() { return totalExams; }
        public void setTotalExams(long totalExams) { this.totalExams = totalExams; }
        public long getCompletedExams() { return completedExams; }
        public void setCompletedExams(long completedExams) { this.completedExams = completedExams; }
        public long getUpcomingExams() { return upcomingExams; }
        public void setUpcomingExams(long upcomingExams) { this.upcomingExams = upcomingExams; }
        public long getPendingResults() { return pendingResults; }
        public void setPendingResults(long pendingResults) { this.pendingResults = pendingResults; }
        public BigDecimal getAverageScore() { return averageScore; }
        public void setAverageScore(BigDecimal averageScore) { this.averageScore = averageScore; }
        public BigDecimal getHighestScore() { return highestScore; }
        public void setHighestScore(BigDecimal highestScore) { this.highestScore = highestScore; }
        public List<String> getExamLabels() { return examLabels; }
        public void setExamLabels(List<String> examLabels) { this.examLabels = examLabels; }
        public List<BigDecimal> getExamScores() { return examScores; }
        public void setExamScores(List<BigDecimal> examScores) { this.examScores = examScores; }
        public Map<String, BigDecimal> getSubjectWiseAverage() { return subjectWiseAverage; }
        public void setSubjectWiseAverage(Map<String, BigDecimal> subjectWiseAverage) { this.subjectWiseAverage = subjectWiseAverage; }
    }

    public static class ProfessorAnalyticsDTO {
        private long totalStudents;
        private long totalExams;
        private long activeExams;
        private long completedExams;
        private BigDecimal classAverageMarks;
        private BigDecimal passPercentage;
        private long pendingEvaluations;
        private long activeProctoringAlerts;
        private List<String> gradeLabels;
        private List<Integer> gradeCounts;
        private List<String> examTitles;
        private List<BigDecimal> examAverages;

        public long getTotalStudents() { return totalStudents; }
        public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }
        public long getTotalExams() { return totalExams; }
        public void setTotalExams(long totalExams) { this.totalExams = totalExams; }
        public long getActiveExams() { return activeExams; }
        public void setActiveExams(long activeExams) { this.activeExams = activeExams; }
        public long getCompletedExams() { return completedExams; }
        public void setCompletedExams(long completedExams) { this.completedExams = completedExams; }
        public BigDecimal getClassAverageMarks() { return classAverageMarks; }
        public void setClassAverageMarks(BigDecimal classAverageMarks) { this.classAverageMarks = classAverageMarks; }
        public BigDecimal getPassPercentage() { return passPercentage; }
        public void setPassPercentage(BigDecimal passPercentage) { this.passPercentage = passPercentage; }
        public long getPendingEvaluations() { return pendingEvaluations; }
        public void setPendingEvaluations(long pendingEvaluations) { this.pendingEvaluations = pendingEvaluations; }
        public long getActiveProctoringAlerts() { return activeProctoringAlerts; }
        public void setActiveProctoringAlerts(long activeProctoringAlerts) { this.activeProctoringAlerts = activeProctoringAlerts; }
        public List<String> getGradeLabels() { return gradeLabels; }
        public void setGradeLabels(List<String> gradeLabels) { this.gradeLabels = gradeLabels; }
        public List<Integer> getGradeCounts() { return gradeCounts; }
        public void setGradeCounts(List<Integer> gradeCounts) { this.gradeCounts = gradeCounts; }
        public List<String> getExamTitles() { return examTitles; }
        public void setExamTitles(List<String> examTitles) { this.examTitles = examTitles; }
        public List<BigDecimal> getExamAverages() { return examAverages; }
        public void setExamAverages(List<BigDecimal> examAverages) { this.examAverages = examAverages; }
    }

    public static class AdminAnalyticsDTO {
        private long totalStudents;
        private long totalProfessors;
        private long pendingProfessors;
        private long totalDepartments;
        private long totalSubjects;
        private long totalExams;
        private long activeExams;
        private long totalAttempts;
        private Map<String, Long> departmentStudentCounts;
        private Map<String, Long> departmentExamCounts;

        public long getTotalStudents() { return totalStudents; }
        public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }
        public long getTotalProfessors() { return totalProfessors; }
        public void setTotalProfessors(long totalProfessors) { this.totalProfessors = totalProfessors; }
        public long getPendingProfessors() { return pendingProfessors; }
        public void setPendingProfessors(long pendingProfessors) { this.pendingProfessors = pendingProfessors; }
        public long getTotalDepartments() { return totalDepartments; }
        public void setTotalDepartments(long totalDepartments) { this.totalDepartments = totalDepartments; }
        public long getTotalSubjects() { return totalSubjects; }
        public void setTotalSubjects(long totalSubjects) { this.totalSubjects = totalSubjects; }
        public long getTotalExams() { return totalExams; }
        public void setTotalExams(long totalExams) { this.totalExams = totalExams; }
        public long getActiveExams() { return activeExams; }
        public void setActiveExams(long activeExams) { this.activeExams = activeExams; }
        public long getTotalAttempts() { return totalAttempts; }
        public void setTotalAttempts(long totalAttempts) { this.totalAttempts = totalAttempts; }
        public Map<String, Long> getDepartmentStudentCounts() { return departmentStudentCounts; }
        public void setDepartmentStudentCounts(Map<String, Long> departmentStudentCounts) { this.departmentStudentCounts = departmentStudentCounts; }
        public Map<String, Long> getDepartmentExamCounts() { return departmentExamCounts; }
        public void setDepartmentExamCounts(Map<String, Long> departmentExamCounts) { this.departmentExamCounts = departmentExamCounts; }
    }
}

