-- ExamGuard AI - MySQL Database Schema
-- Normalized relational schema with foreign keys, indexes, and constraints

CREATE DATABASE IF NOT EXISTS examguard_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE examguard_ai;

-- 1. users
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(30) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    email_otp VARCHAR(10),
    otp_expiry DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. departments
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. subjects
CREATE TABLE IF NOT EXISTS subjects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_id BIGINT NOT NULL,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    credits INT NOT NULL DEFAULT 3,
    semester INT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT,
    INDEX idx_subject_dept (department_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. students
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    register_number VARCHAR(50) NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    current_year INT NOT NULL,
    section VARCHAR(10) NOT NULL,
    profile_image_url VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT,
    INDEX idx_student_reg (register_number),
    INDEX idx_student_dept_yr_sec (department_id, current_year, section)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. professors
CREATE TABLE IF NOT EXISTS professors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    designation VARCHAR(100) NOT NULL,
    approval_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    approved_by_admin_id BIGINT,
    approval_date DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT,
    FOREIGN KEY (approved_by_admin_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_prof_emp (employee_id),
    INDEX idx_prof_status (approval_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. student_faces
CREATE TABLE IF NOT EXISTS student_faces (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL UNIQUE,
    face_template_data LONGTEXT NOT NULL,
    face_image_path VARCHAR(255),
    confidence_score DECIMAL(5,2) DEFAULT 0.0,
    enrollment_status VARCHAR(30) NOT NULL DEFAULT 'ENROLLED',
    consent_given BOOLEAN NOT NULL DEFAULT TRUE,
    enrolled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. exams
CREATE TABLE IF NOT EXISTS exams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    professor_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    instructions TEXT,
    exam_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    duration_minutes INT NOT NULL,
    total_marks DECIMAL(6,2) NOT NULL,
    passing_marks DECIMAL(6,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (professor_id) REFERENCES professors(id) ON DELETE RESTRICT,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE RESTRICT,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT,
    INDEX idx_exam_date (exam_date),
    INDEX idx_exam_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. exam_settings
CREATE TABLE IF NOT EXISTS exam_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL UNIQUE,
    allow_back_nav BOOLEAN NOT NULL DEFAULT TRUE,
    allow_review BOOLEAN NOT NULL DEFAULT TRUE,
    auto_submit BOOLEAN NOT NULL DEFAULT TRUE,
    entry_window_minutes INT NOT NULL DEFAULT 15,
    allow_late_entry BOOLEAN NOT NULL DEFAULT TRUE,
    late_entry_max_minutes INT NOT NULL DEFAULT 30,
    max_attempts INT NOT NULL DEFAULT 1,
    result_publish_date DATETIME,
    shuffle_questions BOOLEAN NOT NULL DEFAULT FALSE,
    shuffle_options BOOLEAN NOT NULL DEFAULT FALSE,
    negative_marking_per_question DECIMAL(4,2) DEFAULT 0.00,
    face_verification_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    continuous_webcam_monitoring BOOLEAN NOT NULL DEFAULT TRUE,
    multiple_face_detection BOOLEAN NOT NULL DEFAULT TRUE,
    face_absence_detection BOOLEAN NOT NULL DEFAULT TRUE,
    screen_share_required BOOLEAN NOT NULL DEFAULT FALSE,
    screen_recording_permitted BOOLEAN NOT NULL DEFAULT FALSE,
    tab_switch_detection BOOLEAN NOT NULL DEFAULT TRUE,
    window_focus_detection BOOLEAN NOT NULL DEFAULT TRUE,
    fullscreen_monitoring BOOLEAN NOT NULL DEFAULT TRUE,
    microphone_monitoring BOOLEAN NOT NULL DEFAULT FALSE,
    periodic_identity_check BOOLEAN NOT NULL DEFAULT FALSE,
    max_warnings INT NOT NULL DEFAULT 5,
    auto_submit_on_violations BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. exam_eligibility
CREATE TABLE IF NOT EXISTS exam_eligibility (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    department_id BIGINT,
    target_year INT,
    target_section VARCHAR(10),
    student_id BIGINT,
    is_excluded BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_elig_exam (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. questions
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subject_id BIGINT NOT NULL,
    professor_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    question_type VARCHAR(30) NOT NULL,
    marks DECIMAL(5,2) NOT NULL DEFAULT 1.0,
    negative_marks DECIMAL(4,2) NOT NULL DEFAULT 0.0,
    difficulty_level VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    unit_number INT DEFAULT 1,
    explanation TEXT,
    image_url VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE RESTRICT,
    FOREIGN KEY (professor_id) REFERENCES professors(id) ON DELETE RESTRICT,
    INDEX idx_q_subject (subject_id),
    INDEX idx_q_difficulty (difficulty_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. question_options
CREATE TABLE IF NOT EXISTS question_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    option_letter VARCHAR(5) NOT NULL,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    INDEX idx_opt_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. exam_questions
CREATE TABLE IF NOT EXISTS exam_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    section_name VARCHAR(100) DEFAULT 'Section A',
    question_order INT NOT NULL DEFAULT 1,
    custom_marks DECIMAL(5,2),
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE RESTRICT,
    UNIQUE KEY uq_exam_question (exam_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. exam_attempts
CREATE TABLE IF NOT EXISTS exam_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    attempt_number INT NOT NULL DEFAULT 1,
    start_time DATETIME NOT NULL,
    server_deadline DATETIME NOT NULL,
    end_time DATETIME,
    submission_type VARCHAR(30) DEFAULT 'NORMAL',
    status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS',
    ip_address VARCHAR(45),
    browser_info VARCHAR(255),
    warning_count INT NOT NULL DEFAULT 0,
    is_paused BOOLEAN NOT NULL DEFAULT FALSE,
    additional_time_minutes INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE RESTRICT,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE RESTRICT,
    INDEX idx_att_exam_student (exam_id, student_id),
    INDEX idx_att_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. student_answers
CREATE TABLE IF NOT EXISTS student_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    selected_option_ids VARCHAR(255),
    text_answer LONGTEXT,
    is_marked_for_review BOOLEAN NOT NULL DEFAULT FALSE,
    auto_evaluated_marks DECIMAL(5,2),
    manual_evaluated_marks DECIMAL(5,2),
    final_marks DECIMAL(5,2),
    evaluation_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    professor_comments TEXT,
    last_saved_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (attempt_id) REFERENCES exam_attempts(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE RESTRICT,
    UNIQUE KEY uq_attempt_question (attempt_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 15. proctoring_events
CREATE TABLE IF NOT EXISTS proctoring_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    exam_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL DEFAULT 'WARNING',
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description TEXT NOT NULL,
    evidence_file_path VARCHAR(255),
    review_status VARCHAR(30) NOT NULL DEFAULT 'UNREVIEWED',
    professor_notes TEXT,
    FOREIGN KEY (attempt_id) REFERENCES exam_attempts(id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    INDEX idx_proc_attempt (attempt_id),
    INDEX idx_proc_severity (severity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 16. recordings
CREATE TABLE IF NOT EXISTS recordings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt_id BIGINT NOT NULL,
    recording_type VARCHAR(30) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_size_bytes BIGINT,
    duration_seconds INT,
    is_encrypted BOOLEAN NOT NULL DEFAULT TRUE,
    recorded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (attempt_id) REFERENCES exam_attempts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 17. late_entry_requests
CREATE TABLE IF NOT EXISTS late_entry_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    decision_reason TEXT,
    professor_id BIGINT,
    valid_until DATETIME,
    requested_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    decided_at DATETIME,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (professor_id) REFERENCES professors(id) ON DELETE SET NULL,
    INDEX idx_late_exam_status (exam_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 18. exam_results
CREATE TABLE IF NOT EXISTS exam_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    attempt_id BIGINT NOT NULL UNIQUE,
    total_marks_obtained DECIMAL(6,2) NOT NULL,
    maximum_marks DECIMAL(6,2) NOT NULL,
    percentage DECIMAL(5,2) NOT NULL,
    grade VARCHAR(5) NOT NULL,
    pass_fail_status VARCHAR(10) NOT NULL,
    class_rank INT,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    published_at DATETIME,
    professor_remarks TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (attempt_id) REFERENCES exam_attempts(id) ON DELETE CASCADE,
    INDEX idx_res_exam (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 19. answer_evaluations
CREATE TABLE IF NOT EXISTS answer_evaluations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_answer_id BIGINT NOT NULL,
    professor_id BIGINT NOT NULL,
    marks_awarded DECIMAL(5,2) NOT NULL,
    feedback TEXT,
    evaluated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_answer_id) REFERENCES student_answers(id) ON DELETE CASCADE,
    FOREIGN KEY (professor_id) REFERENCES professors(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 20. exam_feedback
CREATE TABLE IF NOT EXISTS exam_feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    difficulty_rating INT NOT NULL,
    clarity_rating INT NOT NULL,
    time_sufficiency_rating INT NOT NULL,
    platform_rating INT NOT NULL,
    technical_issues_reported BOOLEAN NOT NULL DEFAULT FALSE,
    suggestions TEXT,
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    UNIQUE KEY uq_feedback_exam_student (exam_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 21. notifications
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    reference_url VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_notif_user_unread (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 22. support_tickets
CREATE TABLE IF NOT EXISTS support_tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subject VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    admin_response TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_ticket_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 23. audit_logs
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id BIGINT,
    details TEXT,
    ip_address VARCHAR(45),
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_audit_time (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 24. login_history
CREATE TABLE IF NOT EXISTS login_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    login_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    status VARCHAR(30) NOT NULL,
    failure_reason VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_login_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

