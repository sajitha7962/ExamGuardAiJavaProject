# ExamGuard AI – Complete REST API Specification & Test Catalog

**Base URL:** `http://localhost:8080/api`  
**Authentication Scheme:** HTTP Authorization Header with `Bearer <JWT_TOKEN>`  
**Standard Response Envelope:**
```json
{
  "success": true,
  "message": "Operation description",
  "data": { ... }
}
```

---

## 1. Authentication & Identity Management (`/api/auth`)

| Endpoint | Method | Required Role | Request Body | Description | Test Result |
| :--- | :---: | :---: | :--- | :--- | :---: |
| `/auth/login` | POST | Public | `LoginRequest` | Authenticates User, returns JWT and user profile | **PASS (200)** |
| `/auth/register/student` | POST | Public | `StudentRegisterRequest` | Registers new student account with college details | **PASS (200)** |
| `/auth/register/professor` | POST | Public | `ProfessorRegisterRequest` | Registers faculty account with PENDING approval | **PASS (200)** |
| `/auth/face-enroll` | POST | STUDENT | `FaceEnrollRequest` | Saves biometric face vector for proctoring checks | **PASS (200)** |
| `/auth/change-password` | POST | Authenticated | `ChangePasswordRequest` | Validates old password and updates BCrypt hash | **PASS (200)** |

---

## 2. Public Metadata Endpoints (`/api/public`)

| Endpoint | Method | Required Role | Description | Test Result |
| :--- | :---: | :---: | :--- | :---: |
| `/public/departments` | GET | Public | Returns all academic departments (CSE, IT, ECE, AIDS) | **PASS (200)** |
| `/public/departments/{id}/subjects` | GET | Public | Returns curriculum subjects for a specific department | **PASS (200)** |
| `/public/subjects` | GET | Public | Lists all active academic subjects | **PASS (200)** |

---

## 3. Student Examination Portal (`/api/student`)

| Endpoint | Method | Required Role | Description | Test Result |
| :--- | :---: | :---: | :--- | :---: |
| `/student/dashboard` | GET | STUDENT | Student KPIs: total exams, completed, pending, average score | **PASS (200)** |
| `/student/profile` | GET | STUDENT | Student personal, academic, and registration details | **PASS (200)** |
| `/student/available-exams` | GET | STUDENT | Lists examinations eligible for student's department/year | **PASS (200)** |
| `/student/exams/{id}` | GET | STUDENT | Instructions, duration, marks, and proctoring rules | **PASS (200)** |
| `/student/exams/{id}/start` | POST | STUDENT | Validates biometric status, initializes attempt & countdown | **PASS (200)** |
| `/student/exams/save-answer` | POST | STUDENT | Real-time debounced answer persistence to MySQL | **PASS (200)** |
| `/student/exams/submit` | POST | STUDENT | Finalizes attempt, calculates objective scores | **PASS (200)** |
| `/student/proctoring/event` | POST | STUDENT | Ingests tab switch, blur, or fullscreen exit incidents | **PASS (200)** |
| `/student/exams/{id}/request-late-entry` | POST | STUDENT | Submits late arrival request to supervising faculty | **PASS (200)** |
| `/student/exams/{id}/results` | GET | STUDENT | Returns published scorecard with question breakdown | **PASS (200)** |
| `/student/exams/feedback` | POST | STUDENT | Ingests candidate examination feedback ratings | **PASS (200)** |
| `/student/support/tickets` | POST | STUDENT | Creates academic or technical support request | **PASS (200)** |
| `/student/support/tickets` | GET | STUDENT | Returns status of submitted support tickets | **PASS (200)** |

---

## 4. Professor Examination & Monitoring Portal (`/api/professor`)

| Endpoint | Method | Required Role | Description | Test Result |
| :--- | :---: | :---: | :--- | :---: |
| `/professor/dashboard` | GET | PROFESSOR | Faculty analytics, pending evaluations, exam statistics | **PASS (200)** |
| `/professor/profile` | GET | PROFESSOR | Faculty academic profile and department assignment | **PASS (200)** |
| `/professor/students` | GET | PROFESSOR | Students enrolled in faculty's department | **PASS (200)** |
| `/professor/exams` | POST | PROFESSOR | Multi-step exam creation wizard (Basic, Settings, Proctoring) | **PASS (200)** |
| `/professor/exams` | GET | PROFESSOR | Lists all examinations authored by this faculty member | **PASS (200)** |
| `/professor/exams/{id}` | GET | PROFESSOR | Detailed configuration of specific examination | **PASS (200)** |
| `/professor/exams/{id}/publish` | POST | PROFESSOR | Transitions exam status to PUBLISHED and alerts students | **PASS (200)** |
| `/professor/questions` | POST | PROFESSOR | Adds question to question bank (MCQ, MSQ, T/F, Descriptive) | **PASS (200)** |
| `/professor/questions` | GET | PROFESSOR | Lists question bank entries authored by faculty | **PASS (200)** |
| `/professor/questions/{id}` | DELETE | PROFESSOR | Safely deletes question if not locked by active exam | **PASS (200)** |
| `/professor/exams/{id}/live-monitoring` | GET | PROFESSOR | Real-time monitoring feed of active student attempts | **PASS (200)** |
| `/professor/attempts/{id}/action` | POST | PROFESSOR | Issues active warning, pauses, or terminates attempt | **PASS (200)** |
| `/professor/exams/{id}/events` | GET | PROFESSOR | Ingested incident audit log for post-exam review | **PASS (200)** |
| `/professor/events/{id}/review` | POST | PROFESSOR | Sets incident status (REVIEWED, DISMISSED, ESCALATED) | **PASS (200)** |
| `/professor/manual-grading` | POST | PROFESSOR | Submits marks and qualitative feedback for descriptive answers | **PASS (200)** |
| `/professor/exams/{id}/publish-results` | POST | PROFESSOR | Publishes finalized results, assigns rank, notifies students | **PASS (200)** |
| `/professor/exams/{id}/results` | GET | PROFESSOR | Ranked list of student scores and pass/fail indicators | **PASS (200)** |
| `/professor/exams/{id}/late-entries` | GET | PROFESSOR | Pending late arrival requests for specific exam | **PASS (200)** |
| `/professor/late-entries/{id}/decide` | POST | PROFESSOR | Approves or rejects late entry with extra minutes | **PASS (200)** |
| `/professor/exams/{id}/feedback` | GET | PROFESSOR | Aggregated ratings and candidate commentary summary | **PASS (200)** |

---

## 5. Administrator Governance Portal (`/api/admin`)

| Endpoint | Method | Required Role | Description | Test Result |
| :--- | :---: | :---: | :--- | :---: |
| `/admin/dashboard` | GET | ADMIN | System-wide statistics, active users, compliance KPIs | **PASS (200)** |
| `/admin/professors` | GET | ADMIN | All faculty registrations with approval status | **PASS (200)** |
| `/admin/professors/{id}/decide` | POST | ADMIN | Approves or rejects pending faculty registrations | **PASS (200)** |
| `/admin/students` | GET | ADMIN | All student accounts and enrollment status | **PASS (200)** |
| `/admin/users/{id}/toggle-active` | POST | ADMIN | Activates or deactivates any system user account | **PASS (200)** |
| `/admin/departments` | POST | ADMIN | Creates academic department | **PASS (200)** |
| `/admin/departments/{id}/subjects` | POST | ADMIN | Adds subject under department curriculum | **PASS (200)** |
| `/admin/audit-logs` | GET | ADMIN | Immutable security and academic audit trail | **PASS (200)** |

---

## 6. Notifications & Common Services (`/api/common`)

| Endpoint | Method | Required Role | Description | Test Result |
| :--- | :---: | :---: | :--- | :---: |
| `/common/notifications` | GET | Authenticated | User's system notifications in reverse chronological order | **PASS (200)** |
| `/common/notifications/unread-count` | GET | Authenticated | Count of unread alerts for navigation badge | **PASS (200)** |
| `/common/notifications/{id}/read` | POST | Authenticated | Marks specific notification as read | **PASS (200)** |
| `/common/notifications/mark-all-read` | POST | Authenticated | Clears all unread badges for the user | **PASS (200)** |

---

## 7. Real-Time WebSocket Protocol (`/ws/proctoring`)

* **Handshake URL:** `ws://localhost:8080/ws/proctoring`
* **Supported Frame Messages:**
  - `REGISTER_STUDENT`: Binds WebSocket session to `attemptId` and `examId`.
  - `HEARTBEAT`: Periodic client telemetry containing `faceDetected: boolean` and timestamp.
  - `PROFESSOR_ACTION`: Server-pushed intervention to student (`WARNING`, `PAUSE`, `TERMINATE`).
* **Test Result:** Verified connection lifecycle, student registration frame, and message broadcasting.

