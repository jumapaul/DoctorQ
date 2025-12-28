package com.doctorq.appointmentservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Slf4j
@Component
public class WebSocketUserInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message, StompHeaderAccessor.class
        );

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String userId = accessor.getFirstNativeHeader("X-User-Id");

            if (userId != null) {
                log.info("Setting user principal: {}", userId);
                accessor.setUser(new Principal() {
                    @Override
                    public String getName() {
                        return userId;
                    }

                    @Override
                    public String toString() {
                        return userId;
                    }
                });

                log.info("User principal set successfully: {}", accessor.getUser().getName());
            }
        }

        return message;
    }
}
