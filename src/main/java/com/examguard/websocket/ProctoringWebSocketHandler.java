package com.examguard.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProctoringWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Map examId -> Set of Professor Sessions
    private final Map<Long, Set<WebSocketSession>> examProfessorSessions = new ConcurrentHashMap<>();

    // Map attemptId -> Student Session
    private final Map<Long, WebSocketSession> studentSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // Handled via registration message
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Map<String, Object> data = objectMapper.readValue(payload, Map.class);
        String type = (String) data.get("type");

        if ("JOIN_MONITOR".equals(type)) {
            // Professor joins monitoring an exam
            Long examId = Long.valueOf(data.get("examId").toString());
            examProfessorSessions.computeIfAbsent(examId, k -> Collections.synchronizedSet(new HashSet<>())).add(session);
            session.sendMessage(new TextMessage("{\"type\":\"MONITOR_CONNECTED\",\"examId\":" + examId + "}"));

        } else if ("REGISTER_STUDENT".equals(type)) {
            // Student connects from exam room
            Long attemptId = Long.valueOf(data.get("attemptId").toString());
            studentSessions.put(attemptId, session);

        } else if ("PROCTORING_ALERT".equals(type) || "HEARTBEAT".equals(type)) {
            // Forward alert or live status from student to professors monitoring this exam
            Long examId = Long.valueOf(data.get("examId").toString());
            Set<WebSocketSession> profSessions = examProfessorSessions.get(examId);
            if (profSessions != null) {
                for (WebSocketSession ps : profSessions) {
                    if (ps.isOpen()) {
                        try {
                            ps.sendMessage(new TextMessage(payload));
                        } catch (IOException ignored) {}
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Cleanup closed sessions
        for (Set<WebSocketSession> set : examProfessorSessions.values()) {
            set.remove(session);
        }
        studentSessions.values().remove(session);
    }
}

