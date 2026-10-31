# ExamGuard AI – Smart Online Examination & AI-Assisted Proctoring Platform

![Java](https://img.shields.io/badge/Java-21%20LTS-287A74?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-55A9A0?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-287A74?style=for-the-badge&logo=springsecurity&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.x%20Ready-55A9A0?style=for-the-badge&logo=mysql&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-Proctoring%20HUD-AEEED3?style=for-the-badge&logo=socketdotio&logoColor=287A74)

ExamGuard AI is a full-stack smart examination and automated proctoring platform engineered for engineering colleges and universities. It integrates real-time browser-based biometric facial verification, Page Visibility tab-switch tracking, Fullscreen enforcement, screen capture monitoring, automated objective scoring, faculty manual evaluation workflows, and role-based portals for **Students**, **Professors**, and **Administrators**.

---

## 🎨 Strict Four-Color Design System

The application strictly adheres to the approved four-color palette with zero third-party framework themes (no Bootstrap, Tailwind, or arbitrary styling):

* **Primary Deep Teal (`#287A74`)**: Main navigation, sidebar headers, primary action buttons, brand emblems.
* **Secondary Ocean Teal (`#55A9A0`)**: Secondary buttons, hover states, chart accents, section highlights.
* **Mint Green (`#AEEED3`)**: Dashboard backgrounds, cards, form inputs, answered palette states, success indicators.
* **Soft Yellow (`#FFF8B0`)**: Incident alerts, proctoring warnings, pending approval notices, marked-for-review tags.

---

## 🚀 Key Features by User Role

### 👨‍🎓 Student Portal
* **Biometric Face Enrollment**: Captures camera frames, extracts normalized 128-d luminance & edge descriptors, and secures one-way biometric identity verification.
* **System Readiness Checklist**: Validates camera, screen sharing authorization, network stability, and fullscreen compatibility before entry.
* **Distraction-Free Exam Interface**: Server-authoritative countdown timer, real-time debounced auto-saving, review flags, and visual palette.
* **Network & Disconnect Recovery**: Seamlessly restores saved answers from relational storage without clock tampering vulnerabilities.
* **Official Examination Scorecard**: Detailed question-by-question breakdown, letter grade, percentile rank, and clean print-to-PDF export.
* **Support & Feedback**: Ingests exam feedback ratings and handles student support tickets.

### 👩‍🏫 Professor Portal
* **Multi-Step Examination Wizard**: Basic details, department/year eligibility, question selection, proctoring configurations, and draft/publish controls.
* **Question Bank Management**: Authoring and CRUD for MCQ, Multi-Select, True/False, Fill-in-the-Blank, and Descriptive questions.
* **Real-Time Proctoring HUD**: Live WebSocket streaming feed of student statuses, warning counts, with active intervention buttons (Warn, Pause, Extend Time, Terminate).
* **Incident Review & Audit**: Post-exam audit log with snapshot evidence, event severity, and classification (Reviewed, Escalated, Dismissed).
* **Evaluation Pipeline**: Instant auto-evaluation of objective questions, alongside a dedicated scoring console for subjective descriptive answers.
* **Late Entry Management**: Review and decide student late-arrival requests with customized time extensions.

### 🏛️ Administrator Portal
* **Faculty Approval Gating**: Restricts unapproved professor accounts from creating or publishing assessments until formally verified.
* **Curriculum Management**: Academic departments and subject code administration.
* **User Management**: System-wide account activation, deactivation, and student enrollment controls.
* **Immutable Audit Trail**: Tracks security events, logins, exam submissions, and result publication timestamps with client IP addresses.

---

## 🛠️ Technology Stack

* **Backend**: Java 21 LTS, Spring Boot 3.3.4, Spring Security, JWT (HMAC-SHA512), Spring Data JPA, Hibernate ORM, WebSocket (`/ws/proctoring`), Spring Mail.
* **Database**: MySQL 8.x schema (`schema-mysql.sql`) with dual-profile support (zero-dependency file-based persistent relational engine in MySQL compatibility mode + native MySQL profile).
* **Frontend**: Semantic HTML5, CSS3, Vanilla JavaScript (ES6+), Fetch API, Canvas Charts (`GuardChart`), WebRTC MediaDevices API, Screen Capture API, Page Visibility API, Fullscreen API.

---

## 🔑 Pre-Seeded Demonstration Accounts

| Role | Email | Password | Pre-Configured State |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@examguard.edu` | `Admin@123` | Chief Superintendent, Full Governance |
| **PROFESSOR** | `prof.smith@examguard.edu` | `Prof@123` | Approved Faculty (CSE), Active Assessment Owner |
| **PROFESSOR (Pending)** | `prof.grace@examguard.edu` | `Prof@123` | Pending Administrator Approval |
| **STUDENT 1** | `student.alex@examguard.edu` | `Student@123` | CSE Year 4, Biometric Face Enrolled |
| **STUDENT 2** | `student.sarah@examguard.edu` | `Student@123` | CSE Year 4, Biometric Face Enrolled |

---

## ⚙️ Quick Start & Running Instructions

### Prerequisites
* Java Development Kit (JDK) 21 LTS installed.
* Git installed.

### 1. Build and Run Application
```bash
# Clone the repository
git clone https://github.com/sajitha7962/ExamGuardAiJavaProject.git
cd ExamGuardAiJavaProject

# Compile and package executable archive
./mvnw clean package -DskipTests

# Run with default persistent relational storage (Zero-dependency out-of-the-box)
java -jar target/examguard-ai-1.0.0.jar
```

### 2. Run with Native MySQL Server (Optional)
To connect to an external MySQL instance:
1. Create the database:
   ```sql
   CREATE DATABASE examguarddb CHARACTER SET utf8mb4;
   ```
2. Run `schema-mysql.sql` to initialize all 24 relational tables.
3. Start the application with the MySQL profile:
   ```bash
   java -jar target/examguard-ai-1.0.0.jar --spring.profiles.active=mysql
   ```

### 3. Access Live Web Application
Open your browser and navigate to:
* **Public Landing Page**: `http://localhost:8080/index.html`
* **Login Portal**: `http://localhost:8080/login.html`

---

## 🧪 Testing & Quality Assurance Reports

Complete quality verification records are documented in the repository:
* [TESTING_REPORT.md](TESTING_REPORT.md) – 139 end-to-end integration and workflow tests (100% PASS).
* [BUG_REPORT.md](BUG_REPORT.md) – Log of all 10 discovered and resolved bugs.
* [API_TEST_REPORT.md](API_TEST_REPORT.md) – Complete catalog of 53 REST endpoints and WebSocket protocols.
* [SECURITY_TEST_REPORT.md](SECURITY_TEST_REPORT.md) – OWASP Top 10 evaluation, IDOR defenses, and biometric privacy audit.
