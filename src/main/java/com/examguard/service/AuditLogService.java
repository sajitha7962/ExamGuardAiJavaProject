package com.examguard.service;

import com.examguard.entity.AuditLog;
import com.examguard.entity.Notification;
import com.examguard.entity.User;
import com.examguard.repository.AuditLogRepository;
import com.examguard.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(User user, String action, String resourceType, Long resourceId, String details, String ipAddress) {
        AuditLog log = new AuditLog(user, action, resourceType, resourceId, details, ipAddress);
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop100ByOrderByTimestampDesc();
    }
}

