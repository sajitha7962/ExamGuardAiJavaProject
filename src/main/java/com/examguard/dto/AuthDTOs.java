package com.examguard.dto;

import com.examguard.entity.enums.Enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthDTOs {

    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class StudentRegisterRequest {
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Register number is required")
        private String registerNumber;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotNull(message = "Department is required")
        private Long departmentId;

        private int currentYear = 1;

        @NotBlank(message = "Section is required")
        private String section;

        private String phoneNumber;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getRegisterNumber() { return registerNumber; }
        public void setRegisterNumber(String registerNumber) { this.registerNumber = registerNumber; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
        public int getCurrentYear() { return currentYear; }
        public void setCurrentYear(int currentYear) { this.currentYear = currentYear; }
        public String getSection() { return section; }
        public void setSection(String section) { this.section = section; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class ProfessorRegisterRequest {
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Employee ID is required")
        private String employeeId;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotNull(message = "Department is required")
        private Long departmentId;

        @NotBlank(message = "Designation is required")
        private String designation;

        private String phoneNumber;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
        public String getDesignation() { return designation; }
        public void setDesignation(String designation) { this.designation = designation; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String token;
        private Long userId;
        private String email;
        private String fullName;
        private Role role;
        private String departmentCode;
        private String registerOrEmployeeId;
        private boolean isApproved;
        private boolean isFaceRegistered;

        public AuthResponse() {}

        public AuthResponse(String token, Long userId, String email, String fullName, Role role,
                            String departmentCode, String registerOrEmployeeId, boolean isApproved, boolean isFaceRegistered) {
            this.token = token;
            this.userId = userId;
            this.email = email;
            this.fullName = fullName;
            this.role = role;
            this.departmentCode = departmentCode;
            this.registerOrEmployeeId = registerOrEmployeeId;
            this.isApproved = isApproved;
            this.isFaceRegistered = isFaceRegistered;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
        public String getDepartmentCode() { return departmentCode; }
        public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }
        public String getRegisterOrEmployeeId() { return registerOrEmployeeId; }
        public void setRegisterOrEmployeeId(String registerOrEmployeeId) { this.registerOrEmployeeId = registerOrEmployeeId; }
        public boolean isApproved() { return isApproved; }
        public void setApproved(boolean approved) { isApproved = approved; }
        public boolean isFaceRegistered() { return isFaceRegistered; }
        public void setFaceRegistered(boolean faceRegistered) { isFaceRegistered = faceRegistered; }
    }

    public static class OtpVerifyRequest {
        @NotBlank private String email;
        @NotBlank private String otp;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOtp() { return otp; }
        public void setOtp(String otp) { this.otp = otp; }
    }

    public static class ForgotPasswordRequest {
        @NotBlank @Email private String email;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class ResetPasswordRequest {
        @NotBlank @Email private String email;
        @NotBlank private String otp;
        @NotBlank @Size(min = 6) private String newPassword;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOtp() { return otp; }
        public void setOtp(String otp) { this.otp = otp; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class ChangePasswordRequest {
        @NotBlank private String currentPassword;
        @NotBlank @Size(min = 6) private String newPassword;

        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class FaceEnrollRequest {
        @NotBlank private String faceTemplateData;
        private String faceImageBase64;
        private boolean consentGiven = true;

        public String getFaceTemplateData() { return faceTemplateData; }
        public void setFaceTemplateData(String faceTemplateData) { this.faceTemplateData = faceTemplateData; }
        public String getFaceImageBase64() { return faceImageBase64; }
        public void setFaceImageBase64(String faceImageBase64) { this.faceImageBase64 = faceImageBase64; }
        public boolean isConsentGiven() { return consentGiven; }
        public void setConsentGiven(boolean consentGiven) { this.consentGiven = consentGiven; }
    }
}

