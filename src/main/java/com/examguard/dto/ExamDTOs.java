package com.examguard.dto;

import com.examguard.entity.enums.Enums.ExamStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ExamDTOs {

    public static class CreateExamRequest {
        @NotNull private Long subjectId;
        @NotNull private Long departmentId;
        @NotBlank private String title;
        private String description;
        private String instructions;
        @NotNull private LocalDate examDate;
        @NotNull private LocalTime startTime;
        @NotNull private LocalTime endTime;
        private int durationMinutes;
        private BigDecimal totalMarks = BigDecimal.valueOf(100.00);
        private BigDecimal passingMarks = BigDecimal.valueOf(40.00);

        // Step 2: Eligibility
        private List<EligibilityDTO> eligibilityList;

        // Step 3: Question IDs
        private List<Long> questionIds;

        // Step 4: Settings
        private ExamSettingsDTO settings;

        // Status
        private ExamStatus status = ExamStatus.DRAFT;

        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getInstructions() { return instructions; }
        public void setInstructions(String instructions) { this.instructions = instructions; }
        public LocalDate getExamDate() { return examDate; }
        public void setExamDate(LocalDate examDate) { this.examDate = examDate; }
        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
        public int getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
        public BigDecimal getTotalMarks() { return totalMarks; }
        public void setTotalMarks(BigDecimal totalMarks) { this.totalMarks = totalMarks; }
        public BigDecimal getPassingMarks() { return passingMarks; }
        public void setPassingMarks(BigDecimal passingMarks) { this.passingMarks = passingMarks; }
        public List<EligibilityDTO> getEligibilityList() { return eligibilityList; }
        public void setEligibilityList(List<EligibilityDTO> eligibilityList) { this.eligibilityList = eligibilityList; }
        public List<Long> getQuestionIds() { return questionIds; }
        public void setQuestionIds(List<Long> questionIds) { this.questionIds = questionIds; }
        public ExamSettingsDTO getSettings() { return settings; }
        public void setSettings(ExamSettingsDTO settings) { this.settings = settings; }
        public ExamStatus getStatus() { return status; }
        public void setStatus(ExamStatus status) { this.status = status; }
    }

    public static class EligibilityDTO {
        private Long departmentId;
        private Integer targetYear;
        private String targetSection;
        private Long studentId;
        private boolean isExcluded;

        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
        public Integer getTargetYear() { return targetYear; }
        public void setTargetYear(Integer targetYear) { this.targetYear = targetYear; }
        public String getTargetSection() { return targetSection; }
        public void setTargetSection(String targetSection) { this.targetSection = targetSection; }
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public boolean isExcluded() { return isExcluded; }
        public void setExcluded(boolean excluded) { isExcluded = excluded; }
    }

    public static class ExamSettingsDTO {
        private boolean allowBackNav = true;
        private boolean allowReview = true;
        private boolean autoSubmit = true;
        private int entryWindowMinutes = 15;
        private boolean allowLateEntry = true;
        private int lateEntryMaxMinutes = 30;
        private int maxAttempts = 1;
        private boolean shuffleQuestions = false;
        private boolean shuffleOptions = false;
        private BigDecimal negativeMarkingPerQuestion = BigDecimal.ZERO;

        // Proctoring
        private boolean faceVerificationEnabled = true;
        private boolean continuousWebcamMonitoring = true;
        private boolean multipleFaceDetection = true;
        private boolean faceAbsenceDetection = true;
        private boolean screenShareRequired = false;
        private boolean screenRecordingPermitted = false;
        private boolean tabSwitchDetection = true;
        private boolean windowFocusDetection = true;
        private boolean fullscreenMonitoring = true;
        private boolean microphoneMonitoring = false;
        private boolean periodicIdentityCheck = false;
        private int maxWarnings = 5;
        private boolean autoSubmitOnViolations = false;

        public boolean isAllowBackNav() { return allowBackNav; }
        public void setAllowBackNav(boolean allowBackNav) { this.allowBackNav = allowBackNav; }
        public boolean isAllowReview() { return allowReview; }
        public void setAllowReview(boolean allowReview) { this.allowReview = allowReview; }
        public boolean isAutoSubmit() { return autoSubmit; }
        public void setAutoSubmit(boolean autoSubmit) { this.autoSubmit = autoSubmit; }
        public int getEntryWindowMinutes() { return entryWindowMinutes; }
        public void setEntryWindowMinutes(int entryWindowMinutes) { this.entryWindowMinutes = entryWindowMinutes; }
        public boolean isAllowLateEntry() { return allowLateEntry; }
        public void setAllowLateEntry(boolean allowLateEntry) { this.allowLateEntry = allowLateEntry; }
        public int getLateEntryMaxMinutes() { return lateEntryMaxMinutes; }
        public void setLateEntryMaxMinutes(int lateEntryMaxMinutes) { this.lateEntryMaxMinutes = lateEntryMaxMinutes; }
        public int getMaxAttempts() { return maxAttempts; }
        public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
        public boolean isShuffleQuestions() { return shuffleQuestions; }
        public void setShuffleQuestions(boolean shuffleQuestions) { this.shuffleQuestions = shuffleQuestions; }
        public boolean isShuffleOptions() { return shuffleOptions; }
        public void setShuffleOptions(boolean shuffleOptions) { this.shuffleOptions = shuffleOptions; }
        public BigDecimal getNegativeMarkingPerQuestion() { return negativeMarkingPerQuestion; }
        public void setNegativeMarkingPerQuestion(BigDecimal negativeMarkingPerQuestion) { this.negativeMarkingPerQuestion = negativeMarkingPerQuestion; }
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
        public boolean isScreenRecordingPermitted() { return screenRecordingPermitted; }
        public void setScreenRecordingPermitted(boolean screenRecordingPermitted) { this.screenRecordingPermitted = screenRecordingPermitted; }
        public boolean isTabSwitchDetection() { return tabSwitchDetection; }
        public void setTabSwitchDetection(boolean tabSwitchDetection) { this.tabSwitchDetection = tabSwitchDetection; }
        public boolean isWindowFocusDetection() { return windowFocusDetection; }
        public void setWindowFocusDetection(boolean windowFocusDetection) { this.windowFocusDetection = windowFocusDetection; }
        public boolean isFullscreenMonitoring() { return fullscreenMonitoring; }
        public void setFullscreenMonitoring(boolean fullscreenMonitoring) { this.fullscreenMonitoring = fullscreenMonitoring; }
        public boolean isMicrophoneMonitoring() { return microphoneMonitoring; }
        public void setMicrophoneMonitoring(boolean microphoneMonitoring) { this.microphoneMonitoring = microphoneMonitoring; }
        public boolean isPeriodicIdentityCheck() { return periodicIdentityCheck; }
        public void setPeriodicIdentityCheck(boolean periodicIdentityCheck) { this.periodicIdentityCheck = periodicIdentityCheck; }
        public int getMaxWarnings() { return maxWarnings; }
        public void setMaxWarnings(int maxWarnings) { this.maxWarnings = maxWarnings; }
        public boolean isAutoSubmitOnViolations() { return autoSubmitOnViolations; }
        public void setAutoSubmitOnViolations(boolean autoSubmitOnViolations) { this.autoSubmitOnViolations = autoSubmitOnViolations; }
    }

    public static class ExamSummaryDTO {
        private Long id;
        private String title;
        private String subjectCode;
        private String subjectName;
        private String departmentName;
        private String professorName;
        private LocalDate examDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private int durationMinutes;
        private BigDecimal totalMarks;
        private BigDecimal passingMarks;
        private ExamStatus status;
        private int questionCount;
        private boolean canStart;
        private boolean requiresLateEntry;
        private String attemptStatus;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSubjectCode() { return subjectCode; }
        public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
        public String getProfessorName() { return professorName; }
        public void setProfessorName(String professorName) { this.professorName = professorName; }
        public LocalDate getExamDate() { return examDate; }
        public void setExamDate(LocalDate examDate) { this.examDate = examDate; }
        public LocalTime getStartTime() { return startTime; }
        public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
        public LocalTime getEndTime() { return endTime; }
        public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
        public int getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
        public BigDecimal getTotalMarks() { return totalMarks; }
        public void setTotalMarks(BigDecimal totalMarks) { this.totalMarks = totalMarks; }
        public BigDecimal getPassingMarks() { return passingMarks; }
        public void setPassingMarks(BigDecimal passingMarks) { this.passingMarks = passingMarks; }
        public ExamStatus getStatus() { return status; }
        public void setStatus(ExamStatus status) { this.status = status; }
        public int getQuestionCount() { return questionCount; }
        public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
        public boolean isCanStart() { return canStart; }
        public void setCanStart(boolean canStart) { this.canStart = canStart; }
        public boolean isRequiresLateEntry() { return requiresLateEntry; }
        public void setRequiresLateEntry(boolean requiresLateEntry) { this.requiresLateEntry = requiresLateEntry; }
        public String getAttemptStatus() { return attemptStatus; }
        public void setAttemptStatus(String attemptStatus) { this.attemptStatus = attemptStatus; }
    }
}

