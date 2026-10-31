package com.examguard.controller;

import com.examguard.dto.ApiResponse;
import com.examguard.dto.AuthDTOs.*;
import com.examguard.security.UserPrincipal;
import com.examguard.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        AuthResponse response = authService.login(req, ip, ua);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/register/student")
    public ResponseEntity<ApiResponse<Void>> registerStudent(@Valid @RequestBody StudentRegisterRequest req) {
        authService.registerStudent(req);
        return ResponseEntity.ok(ApiResponse.success("Student registration successful. You can now log in.", null));
    }

    @PostMapping("/register/professor")
    public ResponseEntity<ApiResponse<Void>> registerProfessor(@Valid @RequestBody ProfessorRegisterRequest req) {
        authService.registerProfessor(req);
        return ResponseEntity.ok(ApiResponse.success("Professor registration submitted for administrator approval.", null));
    }

    @PostMapping("/face-enroll")
    public ResponseEntity<ApiResponse<Void>> enrollFace(@AuthenticationPrincipal UserPrincipal principal,
                                                        @RequestBody FaceEnrollRequest req) {
        authService.enrollFace(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Face identity enrolled and verified successfully.", null));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@AuthenticationPrincipal UserPrincipal principal,
                                                            @Valid @RequestBody ChangePasswordRequest req) {
        authService.changePassword(principal.getId(), req);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully.", null));
    }
}

