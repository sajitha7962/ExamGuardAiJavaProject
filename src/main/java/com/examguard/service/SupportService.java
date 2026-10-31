package com.examguard.service;

import com.examguard.dto.SupportAndNotificationDTOs.*;
import com.examguard.entity.SupportTicket;
import com.examguard.entity.User;
import com.examguard.entity.enums.Enums.TicketStatus;
import com.examguard.exception.ResourceNotFoundException;
import com.examguard.repository.SupportTicketRepository;
import com.examguard.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupportService {

    private final SupportTicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public SupportService(SupportTicketRepository ticketRepository, UserRepository userRepository,
                          NotificationService notificationService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public SupportTicket createTicket(Long userId, CreateTicketRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SupportTicket t = new SupportTicket();
        t.setUser(user);
        t.setSubject(req.getSubject());
        t.setMessage(req.getMessage());
        t.setCategory(req.getCategory());
        t.setPriority(req.getPriority());
        t.setStatus(TicketStatus.OPEN);
        return ticketRepository.save(t);
    }

    @Transactional(readOnly = true)
    public List<SupportTicket> getUserTickets(Long userId) {
        return ticketRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<SupportTicket> getAllOpenTickets() {
        return ticketRepository.findByStatusOrderByCreatedAtDesc(TicketStatus.OPEN);
    }

    @Transactional
    public void respondTicket(Long ticketId, RespondTicketRequest req) {
        SupportTicket t = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        t.setAdminResponse(req.getAdminResponse());
        t.setStatus(req.getStatus());
        if (req.getStatus() == TicketStatus.RESOLVED || req.getStatus() == TicketStatus.CLOSED) {
            t.setResolvedAt(LocalDateTime.now());
        }
        ticketRepository.save(t);

        notificationService.send(t.getUser().getId(), "Support Ticket Updated: " + t.getSubject(),
                "Support team responded: " + req.getAdminResponse(), "SUPPORT_RESPONSE", "/student-support.html");
    }
}

