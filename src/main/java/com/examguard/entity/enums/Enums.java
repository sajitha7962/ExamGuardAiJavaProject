package com.examguard.entity.enums;

public class Enums {

    public enum Role {
        ADMIN, PROFESSOR, STUDENT
    }

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }

    public enum ExamStatus {
        DRAFT, SCHEDULED, PUBLISHED, ONGOING, COMPLETED, CANCELLED
    }

    public enum QuestionType {
        MCQ, MULTI_SELECT, TRUE_FALSE, FILL_BLANK, SHORT_ANSWER, DESCRIPTIVE
    }

    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }

    public enum AttemptStatus {
        IN_PROGRESS, SUBMITTED, EVALUATED, TERMINATED
    }

    public enum SubmissionType {
        NORMAL, AUTO_EXPIRED, VIOLATION_TERMINATED, PROFESSOR_TERMINATED
    }

    public enum EvaluationStatus {
        PENDING, AUTO_EVALUATED, MANUALLY_EVALUATED
    }

    public enum ProctoringEventType {
        TAB_SWITCH, FULLSCREEN_EXIT, FACE_ABSENT, MULTIPLE_FACES, FACE_MISMATCH,
        SCREEN_SHARE_STOPPED, AUDIO_SPIKE, DISCONNECTION, RECONNECTION, WARNING_ISSUED
    }

    public enum Severity {
        INFO, WARNING, HIGH
    }

    public enum ReviewStatus {
        UNREVIEWED, REVIEWED, DISMISSED, ESCALATED
    }

    public enum LateEntryStatus {
        PENDING, APPROVED, REJECTED
    }

    public enum PassFailStatus {
        PASS, FAIL
    }

    public enum TicketStatus {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }

    public enum TicketPriority {
        LOW, MEDIUM, HIGH
    }

    public enum LoginStatus {
        SUCCESS, FAILED, LOCKED
    }
}

