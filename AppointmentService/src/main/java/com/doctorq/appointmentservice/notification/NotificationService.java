package com.doctorq.appointmentservice.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry userRegistry;

    public void sendNotification(Long userId, Notification notification) {

        try {
            String userIdStr = String.valueOf(userId);
            SimpUser user = userRegistry.getUser(userIdStr);

            if (user == null) {
                log.warn("User {} is not connected via WebSocket", userIdStr);
                return;
            }

            messagingTemplate.convertAndSendToUser(
                    userIdStr,
                    "/queue/notifications",
                    notification
            );
            log.info("-----------Notification sent: {}", notification.getType());
        } catch (RuntimeException exception) {
            log.error("----------------> {}", exception.getMessage());
        }
    }
}
