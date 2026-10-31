package com.examguard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class EvaluationAndResultDTOs {

    public static class ManualGradingItem {
        private Long answerId;
        private BigDecimal marksAwarded;
        private String feedback;

        public Long getAnswerId() { return answerId; }
        public void setAnswerId(Long answerId) { this.answerId = answerId; }
        public BigDecimal getMarksAwarded() { return marksAwarded; }
        public void setMarksAwarded(BigDecimal marksAwarded) { this.marksAwarded = marksAwarded; }
        public String getFeedback() { return feedback; }
        public void setFeedback(String feedback) { this.feedback = feedback; }
    }

    public static class ManualGradingSubmitRequest {
        private Long attemptId;
        private List<ManualGradingItem> gradings;
        private String overallRemarks;

        public Long getAttemptId() { return attemptId; }
        public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }
        public List<ManualGradingItem> getGradings() { return gradings; }
        public void setGradings(List<ManualGradingItem> gradings) { this.gradings = gradings; }
        public String getOverallRemarks() { return overallRemarks; }
        public void setOverallRemarks(String overallRemarks) { this.overallRemarks = overallRemarks; }
    }

    public static class StudentResultDetailDTO {
        private Long resultId;
        private Long examId;
        private String examTitle;
        private String subjectCode;
        private String subjectName;
        private String departmentName;
        private String professorName;
        private String studentName;
        private String registerNumber;
        private BigDecimal totalMarksObtained;
        private BigDecimal maximumMarks;
        private BigDecimal percentage;
        private String grade;
        private String passFailStatus;
        private Integer classRank;
        private boolean isPublished;
        private LocalDateTime publishedAt;
        private String professorRemarks;
        private List<ResultQuestionDetailDTO> questionDetails;

        public Long getResultId() { return resultId; }
        public void setResultId(Long resultId) { this.resultId = resultId; }
        public Long getExamId() { return examId; }
        public void setExamId(Long examId) { this.examId = examId; }
        public String getExamTitle() { return examTitle; }
        public void setExamTitle(String examTitle) { this.examTitle = examTitle; }
        public String getSubjectCode() { return subjectCode; }
        public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
        public String getProfessorName() { return professorName; }
        public void setProfessorName(String professorName) { this.professorName = professorName; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getRegisterNumber() { return registerNumber; }
        public void setRegisterNumber(String registerNumber) { this.registerNumber = registerNumber; }
        public BigDecimal getTotalMarksObtained() { return totalMarksObtained; }
        public void setTotalMarksObtained(BigDecimal totalMarksObtained) { this.totalMarksObtained = totalMarksObtained; }
        public BigDecimal getMaximumMarks() { return maximumMarks; }
        public void setMaximumMarks(BigDecimal maximumMarks) { this.maximumMarks = maximumMarks; }
        public BigDecimal getPercentage() { return percentage; }
        public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
        public String getPassFailStatus() { return passFailStatus; }
        public void setPassFailStatus(String passFailStatus) { this.passFailStatus = passFailStatus; }
        public Integer getClassRank() { return classRank; }
        public void setClassRank(Integer classRank) { this.classRank = classRank; }
        public boolean isPublished() { return isPublished; }
        public void setPublished(boolean published) { isPublished = published; }
        public LocalDateTime getPublishedAt() { return publishedAt; }
        public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
        public String getProfessorRemarks() { return professorRemarks; }
        public void setProfessorRemarks(String professorRemarks) { this.professorRemarks = professorRemarks; }
        public List<ResultQuestionDetailDTO> getQuestionDetails() { return questionDetails; }
        public void setQuestionDetails(List<ResultQuestionDetailDTO> questionDetails) { this.questionDetails = questionDetails; }
    }

    public static class ResultQuestionDetailDTO {
        private Long questionId;
        private String questionText;
        private String questionType;
        private BigDecimal maxMarks;
        private BigDecimal marksObtained;
        private String studentAnswer;
        private String correctAnswer;
        private String explanation;
        private String professorComment;

        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public String getQuestionType() { return questionType; }
        public void setQuestionType(String questionType) { this.questionType = questionType; }
        public BigDecimal getMaxMarks() { return maxMarks; }
        public void setMaxMarks(BigDecimal maxMarks) { this.maxMarks = maxMarks; }
        public BigDecimal getMarksObtained() { return marksObtained; }
        public void setMarksObtained(BigDecimal marksObtained) { this.marksObtained = marksObtained; }
        public String getStudentAnswer() { return studentAnswer; }
        public void setStudentAnswer(String studentAnswer) { this.studentAnswer = studentAnswer; }
        public String getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
        public String getProfessorComment() { return professorComment; }
        public void setProfessorComment(String professorComment) { this.professorComment = professorComment; }
    }
}

