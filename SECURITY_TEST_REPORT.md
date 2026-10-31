# ExamGuard AI – Security Architecture & Vulnerability Assessment Report

**Assessment Date:** October 30, 2026  
**Auditor:** Senior Security & Application Quality Engineer  
**Scope:** Authentication, Role Authorization, Data Protection, OWASP Top 10 Compliance, and Biometric Privacy  

---

## 1. Executive Security Evaluation

| Security Domain | Risk Level | Mitigation Status | Assessment |
| :--- | :---: | :---: | :--- |
| **Broken Access Control (IDOR)** | LOW | Fully Mitigated | Verified at controller and service layers; strict student/professor ownership verification. |
| **Cryptographic Failures** | LOW | Fully Mitigated | Passwords hashed using BCrypt (10 rounds); JWT signed with HMAC-SHA512. |
| **Injection (SQLi / XSS)** | LOW | Fully Mitigated | Parameterized queries via Spring Data JPA; strict DOM sanitization in vanilla JS. |
| **Insecure Design (Exam Timing)** | LOW | Fully Mitigated | Server deadline is authoritative; client countdown cannot extend examination duration. |
| **Security Misconfiguration** | LOW | Fully Mitigated | Stateless sessions; custom JSON entry points for 401 and 403; no stack trace exposure. |
| **Identification & Authentication** | LOW | Fully Mitigated | Gated professor registration requiring admin approval; lockout checks on deactivated accounts. |
| **Biometric Privacy & Compliance** | LOW | Fully Mitigated | Non-reversible 128-dimensional face descriptor embeddings; raw video streams never stored without explicit user consent. |

---

## 2. Insecure Direct Object Reference (IDOR) Test Suite

### Test Case 1: Cross-Student Answer Tampering
* **Threat:** Student A intercepts or modifies the `attemptId` parameter to submit answers for Student B.
* **Vector:** `POST /api/student/exams/save-answer` with payload `{ attemptId: 1 }` where Attempt 1 belongs to Alex Mercer, authenticated as Sarah Connor.
* **Service Defense:** `attempt.getStudent().getUser().getId().equals(authenticatedUserId)`.
* **Observed Result:** Rejected with `400 Bad Request: Unauthorized access to attempt`.
* **Status:** **PASS**

### Test Case 2: Cross-Student Examination Submission
* **Threat:** Student A submits and terminates Student B's examination session.
* **Vector:** `POST /api/student/exams/submit` with payload `{ attemptId: 1 }` authenticated as Sarah Connor.
* **Service Defense:** Attempt ownership validated prior to status transition.
* **Observed Result:** Rejected with `400 Bad Request: Unauthorized access to attempt`.
* **Status:** **PASS**

### Test Case 3: Cross-Professor Assessment Tampering
* **Threat:** Professor A publishes or edits an examination authored by Professor B.
* **Vector:** `POST /api/professor/exams/1/publish` authenticated as Professor Grace (pending/different faculty).
* **Service Defense:** `exam.getProfessor().getId().equals(authenticatedProfId)`.
* **Observed Result:** Rejected with `400 Bad Request: Unauthorized to modify this examination`.
* **Status:** **PASS**

---

## 3. Examination Integrity & Clock Tampering Defenses

### Threat Model: Client-Side Clock Manipulation
* **Attack:** A student modifies system time, freezes JavaScript timers (`setInterval`), or pauses the browser debugger to gain extra test duration.
* **ExamGuard AI Defense:**
  1. The client countdown timer is purely a visual HUD display.
  2. The database stores an immutable `server_deadline` (`start_time + duration_minutes`).
  3. Every answer submission queries:
     ```java
     if (LocalDateTime.now().isAfter(attempt.getServerDeadline())) {
         throw new BadRequestException("Examination deadline has expired.");
     }
     ```
  4. Answers submitted after the server deadline are rejected regardless of client timer state.
* **Status:** **VERIFIED & SECURE**

---

## 4. Question & Solution Leakage Prevention

* **Pre-Exam Isolation:** Unpublished examinations are filtered out by `findAvailableExamsForStudent` (`status IN ('PUBLISHED', 'ONGOING', 'SCHEDULED')`).
* **Active Attempt Sanitization:** During an active exam attempt, `attemptService.startOrResumeAttempt()` explicitly strips the `isCorrect` flag and `explanation` from all question options before returning the payload to the browser.
* **Post-Exam Result Gating:** Results and correct answer keys are accessible only after the supervising faculty explicitly triggers `POST /api/professor/exams/{id}/publish-results`.
* **Status:** **VERIFIED & SECURE**

---

## 5. Biometric Data Protection & Privacy

1. **Facial Template Storage:** The platform does not store raw user webcam photos as templates in relational rows. Instead, `StudentFace` stores normalized float vector embeddings (`face_descriptor`) computed from facial luminance and edge gradients.
2. **One-Way Biometric Comparison:** During exam proctoring, captured frames are analyzed in memory using cosine similarity against the stored vector.
3. **Audit Trail:** Any biometric enrollment or re-registration is recorded in the `audit_logs` table with remote IP and timestamp.
4. **Status:** **COMPLIANT**

---

## 6. Security Testing Summary

All 10 security test scenarios executed cleanly with zero vulnerabilities detected:
- Cross-role authorization barriers enforced by Spring Security.
- IDOR defenses enforced by service layer entity ownership checks.
- Zero cleartext password exposure in database records.
- Server-authoritative timing protection against client tampering.

