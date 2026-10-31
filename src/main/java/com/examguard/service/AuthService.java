package com.examguard.service;

import com.examguard.dto.AuthDTOs.*;
import com.examguard.entity.*;
import com.examguard.entity.enums.Enums.ApprovalStatus;
import com.examguard.entity.enums.Enums.LoginStatus;
import com.examguard.entity.enums.Enums.Role;
import com.examguard.exception.BadRequestException;
import com.examguard.exception.ResourceNotFoundException;
import com.examguard.repository.*;
import com.examguard.security.JwtTokenProvider;
import com.examguard.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentFaceRepository studentFaceRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public AuthService(UserRepository userRepository, StudentRepository studentRepository,
                       ProfessorRepository professorRepository, DepartmentRepository departmentRepository,
                       StudentFaceRepository studentFaceRepository, LoginHistoryRepository loginHistoryRepository,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider, AuditLogService auditLogService,
                       NotificationService notificationService) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.departmentRepository = departmentRepository;
        this.studentFaceRepository = studentFaceRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Transactional
    public AuthResponse login(LoginRequest req, String ipAddress, String userAgent) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.isActive()) {
            recordLogin(user, ipAddress, userAgent, LoginStatus.LOCKED, "Account deactivated");
            throw new BadRequestException("Your account has been deactivated. Please contact administrator.");
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            String token = tokenProvider.generateToken(auth);
            recordLogin(user, ipAddress, userAgent, LoginStatus.SUCCESS, null);
            auditLogService.log(user, "USER_LOGIN", "USER", user.getId(), "User logged in", ipAddress);

            String deptCode = "";
            String regOrEmpId = "";
            boolean isApproved = true;
            boolean isFaceRegistered = false;

            if (user.getRole() == Role.STUDENT) {
                Student s = studentRepository.findByUserId(user.getId()).orElse(null);
                if (s != null) {
                    deptCode = s.getDepartment().getCode();
                    regOrEmpId = s.getRegisterNumber();
                    isFaceRegistered = studentFaceRepository.existsByStudentId(s.getId());
                }
            } else if (user.getRole() == Role.PROFESSOR) {
                Professor p = professorRepository.findByUserId(user.getId()).orElse(null);
                if (p != null) {
                    deptCode = p.getDepartment().getCode();
                    regOrEmpId = p.getEmployeeId();
                    isApproved = p.getApprovalStatus() == ApprovalStatus.APPROVED;
                }
            }

            return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(),
                    user.getRole(), deptCode, regOrEmpId, isApproved, isFaceRegistered);

        } catch (Exception ex) {
            recordLogin(user, ipAddress, userAgent, LoginStatus.FAILED, "Bad credentials");
            throw new BadRequestException("Invalid email or password");
        }
    }

    @Transactional
    public void registerStudent(StudentRegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email is already registered.");
        }
        if (studentRepository.findByRegisterNumber(req.getRegisterNumber()).isPresent()) {
            throw new BadRequestException("Register number is already registered.");
        }
        Department dept = departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found."));

        User user = new User();
        user.setEmail(req.getEmail());
        user.setFullName(req.getFullName());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setPhoneNumber(req.getPhoneNumber());
        user.setRole(Role.STUDENT);
        user.setActive(true);
        user.setVerified(true); // For dev/college demo convenience, active and auto-verified
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setEmailOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        Student student = new Student();
        student.setUser(user);
        student.setDepartment(dept);
        student.setRegisterNumber(req.getRegisterNumber());
        student.setCurrentYear(req.getCurrentYear());
        student.setSection(req.getSection());
        studentRepository.save(student);

        auditLogService.log(user, "STUDENT_REGISTRATION", "STUDENT", student.getId(), "Student registered", "LOCAL");
        notificationService.send(user.getId(), "Welcome to ExamGuard AI", 
                "Your student account is active. Please complete face registration before taking exams.", "WELCOME", "/face-registration.html");
    }

    @Transactional
    public void registerProfessor(ProfessorRegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email is already registered.");
        }
        if (professorRepository.findByEmployeeId(req.getEmployeeId()).isPresent()) {
            throw new BadRequestException("Employee ID is already registered.");
        }
        Department dept = departmentRepository.findById(req.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found."));

        User user = new User();
        user.setEmail(req.getEmail());
        user.setFullName(req.getFullName());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setPhoneNumber(req.getPhoneNumber());
        user.setRole(Role.PROFESSOR);
        user.setActive(true);
        user.setVerified(true);
        userRepository.save(user);

        Professor prof = new Professor();
        prof.setUser(user);
        prof.setDepartment(dept);
        prof.setEmployeeId(req.getEmployeeId());
        prof.setDesignation(req.getDesignation());
        prof.setApprovalStatus(ApprovalStatus.PENDING); // Requires admin approval
        professorRepository.save(prof);

        auditLogService.log(user, "PROFESSOR_REGISTRATION", "PROFESSOR", prof.getId(), "Professor registered (Pending Approval)", "LOCAL");
        
        // Notify admins
        userRepository.findByRole(Role.ADMIN).forEach(admin -> {
            notificationService.send(admin.getId(), "New Professor Registration", 
                    prof.getUser().getFullName() + " (" + prof.getEmployeeId() + ") has registered and awaits approval.", 
                    "ADMIN_ALERT", "/manage-professors.html");
        });
    }

    @Transactional
    public void enrollFace(Long userId, FaceEnrollRequest req) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found for user: " + userId));

        StudentFace face = studentFaceRepository.findByStudentId(student.getId()).orElse(new StudentFace());
        face.setStudent(student);
        face.setFaceTemplateData(req.getFaceTemplateData());
        face.setConsentGiven(req.isConsentGiven());
        face.setEnrollmentStatus("ENROLLED");
        face.setUpdatedAt(LocalDateTime.now());
        studentFaceRepository.save(face);

        auditLogService.log(student.getUser(), "FACE_ENROLLED", "STUDENT_FACE", face.getId(), "Face identity biometric enrolled", "CLIENT");
        notificationService.send(userId, "Face Registration Verified", "Your biometric face profile has been registered and verified.", "BIOMETRIC", "/student-profile.html");
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password does not match.");
        }
        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        auditLogService.log(user, "PASSWORD_CHANGED", "USER", user.getId(), "User changed password", "LOCAL");
    }

    private void recordLogin(User user, String ipAddress, String userAgent, LoginStatus status, String reason) {
        LoginHistory history = new LoginHistory(user, ipAddress, userAgent, status, reason);
        loginHistoryRepository.save(history);
    }
}

