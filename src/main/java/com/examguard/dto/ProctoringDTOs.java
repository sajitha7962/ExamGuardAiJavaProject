package com.examguard.dto;

import com.examguard.entity.enums.Enums.ProctoringEventType;
import com.examguard.entity.enums.Enums.Severity;
import java.time.LocalDateTime;

public class ProctoringDTOs {

    public static class EventLogRequest {
        private Long attemptId;
        private Long examId;
        private ProctoringEventType eventType;
        private Severity severity = Severity.WARNING;
        private String description;
        private String evidenceBase64;

        public Long getAttemptId() { return attemptId; }
        public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }
        public Long getExamId() { return examId; }
        public void setExamId(Long examId) { this.examId = examId; }
        public ProctoringEventType getEventType() { return eventType; }
        public void setEventType(ProctoringEventType eventType) { this.eventType = eventType; }
        public Severity getSeverity() { return severity; }
        public void setSeverity(Severity severity) { this.severity = severity; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getEvidenceBase64() { return evidenceBase64; }
        public void setEvidenceBase64(String evidenceBase64) { this.evidenceBase64 = evidenceBase64; }
    }

    public static class StudentLiveStatusDTO {
        private Long attemptId;
        private Long studentId;
        private String studentName;
        private String registerNumber;
        private String departmentName;
        private int currentYear;
        private String section;
        private String profileImageUrl;
        private boolean cameraActive;
        private boolean screenShareActive;
        private boolean faceDetected;
        private boolean multipleFacesDetected;
        private int warningCount;
        private int tabSwitchCount;
        private long remainingSeconds;
        private boolean isPaused;
        private String status;
        private LocalDateTime lastActivity;
        private int riskScore; // 0 to 100

        public Long getAttemptId() { return attemptId; }
        public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getRegisterNumber() { return registerNumber; }
        public void setRegisterNumber(String registerNumber) { this.registerNumber = registerNumber; }
        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
        public int getCurrentYear() { return currentYear; }
        public void setCurrentYear(int currentYear) { this.currentYear = currentYear; }
        public String getSection() { return section; }
        public void setSection(String section) { this.section = section; }
        public String getProfileImageUrl() { return profileImageUrl; }
        public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
        public boolean isCameraActive() { return cameraActive; }
        public void setCameraActive(boolean cameraActive) { this.cameraActive = cameraActive; }
        public boolean isScreenShareActive() { return screenShareActive; }
        public void setScreenShareActive(boolean screenShareActive) { this.screenShareActive = screenShareActive; }
        public boolean isFaceDetected() { return faceDetected; }
        public void setFaceDetected(boolean faceDetected) { this.faceDetected = faceDetected; }
        public boolean isMultipleFacesDetected() { return multipleFacesDetected; }
        public void setMultipleFacesDetected(boolean multipleFacesDetected) { this.multipleFacesDetected = multipleFacesDetected; }
        public int getWarningCount() { return warningCount; }
        public void setWarningCount(int warningCount) { this.warningCount = warningCount; }
        public int getTabSwitchCount() { return tabSwitchCount; }
        public void setTabSwitchCount(int tabSwitchCount) { this.tabSwitchCount = tabSwitchCount; }
        public long getRemainingSeconds() { return remainingSeconds; }
        public void setRemainingSeconds(long remainingSeconds) { this.remainingSeconds = remainingSeconds; }
        public boolean isPaused() { return isPaused; }
        public void setPaused(boolean paused) { isPaused = paused; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public LocalDateTime getLastActivity() { return lastActivity; }
        public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
        public int getRiskScore() { return riskScore; }
        public void setRiskScore(int riskScore) { this.riskScore = riskScore; }
    }

    public static class ProfessorActionRequest {
        private String action; // WARNING, PAUSE, RESUME, EXTEND_TIME, TERMINATE
        private String message;
        private int additionalMinutes;

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public int getAdditionalMinutes() { return additionalMinutes; }
        public void setAdditionalMinutes(int additionalMinutes) { this.additionalMinutes = additionalMinutes; }
    }
}

