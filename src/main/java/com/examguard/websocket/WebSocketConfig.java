package com.examguard.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ProctoringWebSocketHandler proctoringWebSocketHandler;

    public WebSocketConfig(ProctoringWebSocketHandler proctoringWebSocketHandler) {
        this.proctoringWebSocketHandler = proctoringWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(proctoringWebSocketHandler, "/ws/proctoring")
                .setAllowedOrigins("*");
    }
}

