package com.examguard;

import com.examguard.dto.AuthDTOs.LoginRequest;
import com.examguard.dto.AuthDTOs.AuthResponse;
import com.examguard.entity.Exam;
import com.examguard.entity.enums.Enums.ExamStatus;
import com.examguard.repository.ExamRepository;
import com.examguard.repository.UserRepository;
import com.examguard.service.AuthService;
import com.examguard.service.ExamService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExamGuardApplicationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamRepository examRepository;

    @Test
    @DisplayName("Verify seed data and admin account initialized successfully")
    void contextLoadsAndSeedDataExists() {
        assertTrue(userRepository.count() >= 3, "Seed users should exist");
        assertTrue(userRepository.findByEmail("admin@examguard.edu").isPresent(), "Admin user must be present");
        assertTrue(userRepository.findByEmail("prof.smith@examguard.edu").isPresent(), "Professor must be present");
        assertTrue(userRepository.findByEmail("student.alex@examguard.edu").isPresent(), "Student must be present");
    }

    @Test
    @DisplayName("Verify student login authentication and role check")
    void testStudentLogin() {
        LoginRequest req = new LoginRequest();
        req.setEmail("student.alex@examguard.edu");
        req.setPassword("Student@123");

        AuthResponse resp = authService.login(req, "127.0.0.1", "JUnit-Test");
        assertNotNull(resp);
        assertNotNull(resp.getToken());
        assertEquals("Alex Mercer", resp.getFullName());
        assertTrue(resp.isFaceRegistered());
    }

    @Test
    @DisplayName("Verify exams are queryable by department and published status")
    void testExamsAvailability() {
        assertFalse(examRepository.findByStatus(ExamStatus.PUBLISHED).isEmpty(), "Published exams should exist");
    }

    @Test
    @DisplayName("Verify professor login and approval state")
    void testProfessorLogin() {
        LoginRequest req = new LoginRequest();
        req.setEmail("prof.smith@examguard.edu");
        req.setPassword("Prof@123");

        AuthResponse resp = authService.login(req, "127.0.0.1", "JUnit-Test");
        assertNotNull(resp);
        assertEquals("Prof. Alan Turing", resp.getFullName());
        assertTrue(resp.isApproved(), "Prof Smith must be approved");
    }

    @Test
    @DisplayName("Verify admin login")
    void testAdminLogin() {
        LoginRequest req = new LoginRequest();
        req.setEmail("admin@examguard.edu");
        req.setPassword("Admin@123");

        AuthResponse resp = authService.login(req, "127.0.0.1", "JUnit-Test");
        assertNotNull(resp);
        assertEquals("Dr. Aris Thorne (Chief Exam Superintendent)", resp.getFullName());
        assertEquals(com.examguard.entity.enums.Enums.Role.ADMIN, resp.getRole());
    }

    @Test
    @DisplayName("Verify invalid credentials rejection")
    void testInvalidCredentialsRejection() {
        LoginRequest req = new LoginRequest();
        req.setEmail("student.alex@examguard.edu");
        req.setPassword("WrongPassword@999");

        assertThrows(Exception.class, () -> authService.login(req, "127.0.0.1", "JUnit-Test"));
    }
}

