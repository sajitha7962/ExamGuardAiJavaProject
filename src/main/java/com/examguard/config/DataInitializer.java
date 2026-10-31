package com.examguard.config;

import com.examguard.entity.*;
import com.examguard.entity.enums.Enums.*;
import com.examguard.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final StudentFaceRepository studentFaceRepository;
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamEligibilityRepository eligibilityRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, DepartmentRepository departmentRepository,
            SubjectRepository subjectRepository, StudentRepository studentRepository,
            ProfessorRepository professorRepository, StudentFaceRepository studentFaceRepository,
            QuestionRepository questionRepository, ExamRepository examRepository,
            ExamQuestionRepository examQuestionRepository,
            ExamEligibilityRepository eligibilityRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.studentFaceRepository = studentFaceRepository;
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.eligibilityRepository = eligibilityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Seed data already exists
        }

        // 1. Create Departments
        Department cse = departmentRepository
                .save(new Department("CSE", "Computer Science & Engineering", "Department of CSE"));
        Department it = departmentRepository
                .save(new Department("IT", "Information Technology", "Department of Information Technology"));
        Department ece = departmentRepository
                .save(new Department("ECE", "Electronics & Communication Engineering", "Department of ECE"));
        Department aids = departmentRepository
                .save(new Department("AIDS", "Artificial Intelligence & Data Science", "Department of AI & DS"));

        // 2. Create Subjects
        Subject cs801 = subjectRepository
                .save(new Subject(cse, "CS801", "Distributed Systems & Cloud Computing", 4, 7));
        Subject cs802 = subjectRepository.save(new Subject(cse, "CS802", "Database Management Systems", 4, 3));
        Subject ai501 = subjectRepository.save(new Subject(aids, "AI501", "Machine Learning & Neural Networks", 4, 5));
        Subject it401 = subjectRepository.save(new Subject(it, "IT401", "Web Technologies & Security", 3, 4));

        // 3. Create Admin User
        User adminUser = new User();
        adminUser.setEmail("admin@examguard.edu");
        adminUser.setFullName("Dr. Aris Thorne (Chief Exam Superintendent)");
        adminUser.setPasswordHash(passwordEncoder.encode("Admin@123"));
        adminUser.setPhoneNumber("+91 98765 00001");
        adminUser.setRole(Role.ADMIN);
        adminUser.setActive(true);
        adminUser.setVerified(true);
        userRepository.save(adminUser);

        // 4. Create Professor 1 (Approved)
        User profUser1 = new User();
        profUser1.setEmail("prof.smith@examguard.edu");
        profUser1.setFullName("Prof. Alan Turing");
        profUser1.setPasswordHash(passwordEncoder.encode("Prof@123"));
        profUser1.setPhoneNumber("+91 98765 00002");
        profUser1.setRole(Role.PROFESSOR);
        profUser1.setActive(true);
        profUser1.setVerified(true);
        userRepository.save(profUser1);

        Professor prof1 = new Professor();
        prof1.setUser(profUser1);
        prof1.setDepartment(cse);
        prof1.setEmployeeId("EMP-CSE-101");
        prof1.setDesignation("Associate Professor & Head of Assessment");
        prof1.setApprovalStatus(ApprovalStatus.APPROVED);
        prof1.setApprovedByAdmin(adminUser);
        prof1.setApprovalDate(LocalDateTime.now().minusDays(10));
        professorRepository.save(prof1);

        // 5. Create Professor 2 (Pending Approval)
        User profUser2 = new User();
        profUser2.setEmail("prof.grace@examguard.edu");
        profUser2.setFullName("Dr. Grace Hopper");
        profUser2.setPasswordHash(passwordEncoder.encode("Prof@123"));
        profUser2.setPhoneNumber("+91 98765 00003");
        profUser2.setRole(Role.PROFESSOR);
        profUser2.setActive(true);
        profUser2.setVerified(true);
        userRepository.save(profUser2);

        Professor prof2 = new Professor();
        prof2.setUser(profUser2);
        prof2.setDepartment(aids);
        prof2.setEmployeeId("EMP-AIDS-202");
        prof2.setDesignation("Assistant Professor");
        prof2.setApprovalStatus(ApprovalStatus.PENDING);
        professorRepository.save(prof2);

        // 6. Create Student 1
        User studentUser1 = new User();
        studentUser1.setEmail("student.alex@examguard.edu");
        studentUser1.setFullName("Alex Mercer");
        studentUser1.setPasswordHash(passwordEncoder.encode("Student@123"));
        studentUser1.setPhoneNumber("+91 98765 11111");
        studentUser1.setRole(Role.STUDENT);
        studentUser1.setActive(true);
        studentUser1.setVerified(true);
        userRepository.save(studentUser1);

        Student student1 = new Student();
        student1.setUser(studentUser1);
        student1.setDepartment(cse);
        student1.setRegisterNumber("REG2024CSE01");
        student1.setCurrentYear(4);
        student1.setSection("A");
        studentRepository.save(student1);

        // Seed face enrollment for student 1
        StudentFace sf = new StudentFace();
        sf.setStudent(student1);
        sf.setFaceTemplateData(
                "{\"descriptor\":[0.12,-0.45,0.78,0.22,0.91,-0.11,0.33,0.67,0.14,-0.52],\"confidence\":98.5,\"algorithm\":\"HOG_LANDMARKS_v1\"}");
        sf.setConfidenceScore(BigDecimal.valueOf(98.50));
        sf.setEnrollmentStatus("ENROLLED");
        sf.setConsentGiven(true);
        studentFaceRepository.save(sf);

        // 7. Create Student 2
        User studentUser2 = new User();
        studentUser2.setEmail("student.sarah@examguard.edu");
        studentUser2.setFullName("Sarah Connor");
        studentUser2.setPasswordHash(passwordEncoder.encode("Student@123"));
        studentUser2.setPhoneNumber("+91 98765 22222");
        studentUser2.setRole(Role.STUDENT);
        studentUser2.setActive(true);
        studentUser2.setVerified(true);
        userRepository.save(studentUser2);

        Student student2 = new Student();
        student2.setUser(studentUser2);
        student2.setDepartment(cse);
        student2.setRegisterNumber("REG2024CSE02");
        student2.setCurrentYear(4);
        student2.setSection("A");
        studentRepository.save(student2);

        // 8. Create Questions for CS801
        Question q1 = new Question();
        q1.setSubject(cs801);
        q1.setProfessor(prof1);
        q1.setQuestionText("In the CAP theorem for distributed systems, what does the letter 'P' stand for?");
        q1.setQuestionType(QuestionType.MCQ);
        q1.setMarks(BigDecimal.valueOf(2.00));
        q1.setNegativeMarks(BigDecimal.valueOf(0.50));
        q1.setDifficultyLevel(DifficultyLevel.EASY);
        q1.setExplanation("CAP stands for Consistency, Availability, and Partition Tolerance.");
        q1.addOption(new QuestionOption("A", "Process Isolation", false));
        q1.addOption(new QuestionOption("B", "Partition Tolerance", true));
        q1.addOption(new QuestionOption("C", "Performance Reliability", false));
        q1.addOption(new QuestionOption("D", "Persistence Guarantee", false));
        questionRepository.save(q1);

        Question q2 = new Question();
        q2.setSubject(cs801);
        q2.setProfessor(prof1);
        q2.setQuestionText("Which of the following algorithms are used to achieve consensus in distributed systems?");
        q2.setQuestionType(QuestionType.MULTI_SELECT);
        q2.setMarks(BigDecimal.valueOf(3.00));
        q2.setDifficultyLevel(DifficultyLevel.MEDIUM);
        q2.setExplanation("Raft and Paxos are renowned distributed consensus protocols.");
        q2.addOption(new QuestionOption("A", "Paxos", true));
        q2.addOption(new QuestionOption("B", "Raft", true));
        q2.addOption(new QuestionOption("C", "Dijkstra's Shortest Path", false));
        q2.addOption(new QuestionOption("D", "QuickSort", false));
        questionRepository.save(q2);

        Question q3 = new Question();
        q3.setSubject(cs801);
        q3.setProfessor(prof1);
        q3.setQuestionText(
                "True or False: In a distributed system with eventual consistency, read operations are guaranteed to immediately return the latest write across all replicas.");
        q3.setQuestionType(QuestionType.TRUE_FALSE);
        q3.setMarks(BigDecimal.valueOf(2.00));
        q3.setDifficultyLevel(DifficultyLevel.EASY);
        q3.setExplanation(
                "In eventual consistency, replicas synchronize asynchronously over time; immediate strict consistency is not guaranteed.");
        q3.addOption(new QuestionOption("A", "True", false));
        q3.addOption(new QuestionOption("B", "False", true));
        questionRepository.save(q3);

        Question q4 = new Question();
        q4.setSubject(cs801);
        q4.setProfessor(prof1);
        q4.setQuestionText(
                "The consensus protocol designed as an understandable alternative to Paxos is called _____.");
        q4.setQuestionType(QuestionType.FILL_BLANK);
        q4.setMarks(BigDecimal.valueOf(3.00));
        q4.setDifficultyLevel(DifficultyLevel.MEDIUM);
        q4.setExplanation("Raft was created by Ongaro and Ousterhout for enhanced understandability.");
        q4.addOption(new QuestionOption("A", "Raft", true));
        questionRepository.save(q4);

        Question q5 = new Question();
        q5.setSubject(cs801);
        q5.setProfessor(prof1);
        q5.setQuestionText(
                "Explain the Two-Phase Commit (2PC) protocol in distributed databases, detailing the Prepare and Commit phases, and identify its primary failure mode.");
        q5.setQuestionType(QuestionType.DESCRIPTIVE);
        q5.setMarks(BigDecimal.valueOf(10.00));
        q5.setDifficultyLevel(DifficultyLevel.HARD);
        q5.setExplanation(
                "2PC involves Coordinator and Participants. Phase 1: Prepare vote. Phase 2: Global Commit/Abort. Primary failure mode: Coordinator crash causing blocking.");
        questionRepository.save(q5);

        // 9. Create an Active Exam (Available for Student 1 and Student 2)
        Exam exam1 = new Exam();
        exam1.setProfessor(prof1);
        exam1.setSubject(cs801);
        exam1.setDepartment(cse);
        exam1.setTitle("Distributed Systems & Cloud Final Assessment 2026");
        exam1.setDescription(
                "Official university examination covering CAP Theorem, Consensus, Two-Phase Commit, and Cloud Architectures.");
        exam1.setInstructions(
                "1. Webcam and fullscreen mode are mandatory.\n2. Do not switch tabs or exit fullscreen.\n3. Keep your face centered in the camera frame.\n4. Answers are auto-saved in real-time.");
        exam1.setExamDate(LocalDate.now());
        exam1.setStartTime(LocalTime.MIN); // Available all day for seamless evaluation
        exam1.setEndTime(LocalTime.MAX);
        exam1.setDurationMinutes(60);
        exam1.setTotalMarks(BigDecimal.valueOf(20.00));
        exam1.setPassingMarks(BigDecimal.valueOf(8.00));
        exam1.setStatus(ExamStatus.PUBLISHED);

        ExamSettings settings = new ExamSettings();
        settings.setAllowBackNav(true);
        settings.setAllowReview(true);
        settings.setAutoSubmit(true);
        settings.setEntryWindowMinutes(1440); // 24 hours open window for testing
        settings.setFaceVerificationEnabled(true);
        settings.setContinuousWebcamMonitoring(true);
        settings.setMultipleFaceDetection(true);
        settings.setFaceAbsenceDetection(true);
        settings.setTabSwitchDetection(true);
        settings.setFullscreenMonitoring(true);
        settings.setMaxWarnings(5);
        exam1.setSettings(settings);

        Exam savedExam = examRepository.save(exam1);

        // Eligibility for CSE department
        ExamEligibility eligibility = new ExamEligibility();
        eligibility.setExam(savedExam);
        eligibility.setDepartment(cse);
        eligibilityRepository.save(eligibility);

        // Link questions
        int order = 1;
        for (Question q : List.of(q1, q2, q3, q4, q5)) {
            examQuestionRepository.save(new ExamQuestion(savedExam, q, "Section A", order++, q.getMarks()));
        }
    }
}
