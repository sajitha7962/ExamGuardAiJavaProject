package com.examguard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "exam_settings")
public class ExamSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false, unique = true)
    private Exam exam;

    @Column(nullable = false)
    private boolean allowBackNav = true;

    @Column(nullable = false)
    private boolean allowReview = true;

    @Column(nullable = false)
    private boolean autoSubmit = true;

    @Column(nullable = false)
    private int entryWindowMinutes = 15;

    @Column(nullable = false)
    private boolean allowLateEntry = true;

    @Column(nullable = false)
    private int lateEntryMaxMinutes = 30;

    @Column(nullable = false)
    private int maxAttempts = 1;

    private java.time.LocalDateTime resultPublishDate;

    @Column(nullable = false)
    private boolean shuffleQuestions = false;

    @Column(nullable = false)
    private boolean shuffleOptions = false;

    @Column(precision = 4, scale = 2)
    private BigDecimal negativeMarkingPerQuestion = BigDecimal.ZERO;

    // Proctoring Controls
    @Column(nullable = false)
    private boolean faceVerificationEnabled = true;

    @Column(nullable = false)
    private boolean continuousWebcamMonitoring = true;

    @Column(nullable = false)
    private boolean multipleFaceDetection = true;

    @Column(nullable = false)
    private boolean faceAbsenceDetection = true;

    @Column(nullable = false)
    private boolean screenShareRequired = false;

    @Column(nullable = false)
    private boolean screenRecordingPermitted = false;

    @Column(nullable = false)
    private boolean tabSwitchDetection = true;

    @Column(nullable = false)
    private boolean windowFocusDetection = true;

    @Column(nullable = false)
    private boolean fullscreenMonitoring = true;

    @Column(nullable = false)
    private boolean microphoneMonitoring = false;

    @Column(nullable = false)
    private boolean periodicIdentityCheck = false;

    @Column(nullable = false)
    private int maxWarnings = 5;

    @Column(nullable = false)
    private boolean autoSubmitOnViolations = false;

    public ExamSettings() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }
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
    public java.time.LocalDateTime getResultPublishDate() { return resultPublishDate; }
    public void setResultPublishDate(java.time.LocalDateTime resultPublishDate) { this.resultPublishDate = resultPublishDate; }
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

