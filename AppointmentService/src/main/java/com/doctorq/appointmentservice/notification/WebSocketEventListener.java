package com.doctorq.appointmentservice.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@Slf4j
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "unknown";
        log.info("WebSocket Connected: User {} - Session {}", userId, headerAccessor.getSessionId());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "unknown";
        log.info("WebSocket Disconnected: User {} - Session {}", userId, headerAccessor.getSessionId());
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "unknown";
        String destination = headerAccessor.getDestination();
        log.info("WebSocket Subscription: User {} subscribed to {}", userId, destination);
    }
}