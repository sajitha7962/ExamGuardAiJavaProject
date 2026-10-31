package com.examguard.dto;

import com.examguard.entity.enums.Enums.TicketPriority;
import com.examguard.entity.enums.Enums.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class SupportAndNotificationDTOs {

    public static class CreateTicketRequest {
        @NotBlank private String subject;
        @NotBlank private String message;
        private String category = "EXAM_ISSUE";
        private TicketPriority priority = TicketPriority.MEDIUM;

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public TicketPriority getPriority() { return priority; }
        public void setPriority(TicketPriority priority) { this.priority = priority; }
    }

    public static class RespondTicketRequest {
        @NotBlank private String adminResponse;
        private TicketStatus status = TicketStatus.RESOLVED;

        public String getAdminResponse() { return adminResponse; }
        public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }
        public TicketStatus getStatus() { return status; }
        public void setStatus(TicketStatus status) { this.status = status; }
    }

    public static class NotificationDTO {
        private Long id;
        private String title;
        private String message;
        private String notificationType;
        private boolean isRead;
        private String referenceUrl;
        private LocalDateTime createdAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getNotificationType() { return notificationType; }
        public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
        public boolean isRead() { return isRead; }
        public void setRead(boolean read) { isRead = read; }
        public String getReferenceUrl() { return referenceUrl; }
        public void setReferenceUrl(String referenceUrl) { this.referenceUrl = referenceUrl; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}

