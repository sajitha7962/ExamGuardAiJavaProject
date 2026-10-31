package com.examguard.dto;

import com.examguard.entity.enums.Enums.AttemptStatus;
import com.examguard.entity.enums.Enums.QuestionType;
import com.examguard.entity.enums.Enums.SubmissionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AttemptDTOs {

    public static class StartAttemptResponse {
        private Long attemptId;
        private Long examId;
        private String examTitle;
        private String subjectName;
        private LocalDateTime startTime;
        private LocalDateTime serverDeadline;
        private long remainingSeconds;
        private boolean allowBackNav;
        private boolean allowReview;
        private boolean autoSubmit;
        private int maxWarnings;
        private boolean faceVerificationEnabled;
        private boolean continuousWebcamMonitoring;
        private boolean multipleFaceDetection;
        private boolean faceAbsenceDetection;
        private boolean screenShareRequired;
        private boolean tabSwitchDetection;
        private boolean fullscreenMonitoring;
        private List<ExamRoomQuestionDTO> questions;

        public Long getAttemptId() { return attemptId; }
        public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }
        public Long getExamId() { return examId; }
        public void setExamId(Long examId) { this.examId = examId; }
        public String getExamTitle() { return examTitle; }
        public void setExamTitle(String examTitle) { this.examTitle = examTitle; }
        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getServerDeadline() { return serverDeadline; }
        public void setServerDeadline(LocalDateTime serverDeadline) { this.serverDeadline = serverDeadline; }
        public long getRemainingSeconds() { return remainingSeconds; }
        public void setRemainingSeconds(long remainingSeconds) { this.remainingSeconds = remainingSeconds; }
        public boolean isAllowBackNav() { return allowBackNav; }
        public void setAllowBackNav(boolean allowBackNav) { this.allowBackNav = allowBackNav; }
        public boolean isAllowReview() { return allowReview; }
        public void setAllowReview(boolean allowReview) { this.allowReview = allowReview; }
        public boolean isAutoSubmit() { return autoSubmit; }
        public void setAutoSubmit(boolean autoSubmit) { this.autoSubmit = autoSubmit; }
        public int getMaxWarnings() { return maxWarnings; }
        public void setMaxWarnings(int maxWarnings) { this.maxWarnings = maxWarnings; }
        public boolean isFaceVerificationEnabled() { return faceVerificationEnabled; }
        public void setFaceVerificationEnabled(boolean faceVerificationEnabled) { this.faceVerificationEnabled = faceVerificationEnabled; }
        public boolean isContinuousWebcamMonitoring() { return continuousWebcamMonitoring; }
        public void setContinuousWebcamMonitoring(boolean continuousWebcamMonitoring) { this.continuousWebcamMonitoring = continuousWebcamMonitoring; }
        public boolean isMultipleFaceDetection() { return multipleFaceDetection; }
        public void setMultipleFaceDetection(boolean multipleFaceDetection) { this.multipleFaceDetection = multipleFaceDetection; }
        public boolean isFaceAbsenceDetection() { return faceAbsenceDetection; }
        public void setFaceAbsenceDetection(boolean faceAbsenceDetection) { this.faceAbsenceDetection = faceAbsenceDetection; }
        public boolean isScreenShareRequired() { return screenShareRequired; }
        public void setScreenShareRequired(boolean screenShareRequired) { this.screenShareRequired = screenShareRequired; }
        public boolean isTabSwitchDetection() { return tabSwitchDetection; }
        public void setTabSwitchDetection(boolean tabSwitchDetection) { this.tabSwitchDetection = tabSwitchDetection; }
        public boolean isFullscreenMonitoring() { return fullscreenMonitoring; }
        public void setFullscreenMonitoring(boolean fullscreenMonitoring) { this.fullscreenMonitoring = fullscreenMonitoring; }
        public List<ExamRoomQuestionDTO> getQuestions() { return questions; }
        public void setQuestions(List<ExamRoomQuestionDTO> questions) { this.questions = questions; }
    }

    public static class ExamRoomQuestionDTO {
        private Long questionId;
        private int questionOrder;
        private String sectionName;
        private String questionText;
        private QuestionType questionType;
        private BigDecimal marks;
        private BigDecimal negativeMarks;
        private String imageUrl;
        private List<ExamRoomOptionDTO> options;
        private String savedSelectedOptions;
        private String savedTextAnswer;
        private boolean isMarkedForReview;

        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public int getQuestionOrder() { return questionOrder; }
        public void setQuestionOrder(int questionOrder) { this.questionOrder = questionOrder; }
        public String getSectionName() { return sectionName; }
        public void setSectionName(String sectionName) { this.sectionName = sectionName; }
        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public QuestionType getQuestionType() { return questionType; }
        public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }
        public BigDecimal getMarks() { return marks; }
        public void setMarks(BigDecimal marks) { this.marks = marks; }
        public BigDecimal getNegativeMarks() { return negativeMarks; }
        public void setNegativeMarks(BigDecimal negativeMarks) { this.negativeMarks = negativeMarks; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public List<ExamRoomOptionDTO> getOptions() { return options; }
        public void setOptions(List<ExamRoomOptionDTO> options) { this.options = options; }
        public String getSavedSelectedOptions() { return savedSelectedOptions; }
        public void setSavedSelectedOptions(String savedSelectedOptions) { this.savedSelectedOptions = savedSelectedOptions; }
        public String getSavedTextAnswer() { return savedTextAnswer; }
        public void setSavedTextAnswer(String savedTextAnswer) { this.savedTextAnswer = savedTextAnswer; }
        public boolean isMarkedForReview() { return isMarkedForReview; }
        public void setMarkedForReview(boolean markedForReview) { isMarkedForReview = markedForReview; }
    }

    public static class ExamRoomOptionDTO {
        private Long id;
        private String optionLetter;
        private String optionText;

        public ExamRoomOptionDTO() {}
        public ExamRoomOptionDTO(Long id, String optionLetter, String optionText) {
            this.id = id;
            this.optionLetter = optionLetter;
            this.optionText = optionText;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getOptionLetter() { return optionLetter; }
        public void setOptionLetter(String optionLetter) { this.optionLetter = optionLetter; }
        public String getOptionText() { return optionText; }
        public void setOptionText(String optionText) { this.optionText = optionText; }
    }

    public static class SaveAnswerRequest {
        private Long attemptId;
        private Long questionId;
        private String selectedOptionIds;
        private String textAnswer;
        private boolean isMarkedForReview;

        public Long getAttemptId() { return attemptId; }
        public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }
        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public String getSelectedOptionIds() { return selectedOptionIds; }
        public void setSelectedOptionIds(String selectedOptionIds) { this.selectedOptionIds = selectedOptionIds; }
        public String getTextAnswer() { return textAnswer; }
        public void setTextAnswer(String textAnswer) { this.textAnswer = textAnswer; }
        public boolean isMarkedForReview() { return isMarkedForReview; }
        public void setMarkedForReview(boolean markedForReview) { isMarkedForReview = markedForReview; }
    }

    public static class SubmitAttemptRequest {
        private Long attemptId;
        private SubmissionType submissionType = SubmissionType.NORMAL;

        public Long getAttemptId() { return attemptId; }
        public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }
        public SubmissionType getSubmissionType() { return submissionType; }
        public void setSubmissionType(SubmissionType submissionType) { this.submissionType = submissionType; }
    }
}

