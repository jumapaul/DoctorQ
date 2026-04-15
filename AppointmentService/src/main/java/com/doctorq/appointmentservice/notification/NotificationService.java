package com.doctorq.appointmentservice.notification;

import com.doctorq.appointmentservice.exception.FirebaseMessaginException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Async
    public void sendNotification(NotificationRequest request) {
        String topic = "user_" + request.getUserId();
        try {
            Message message = Message.builder()
                    .setTopic(topic)
                    .setNotification(Notification.builder()
                            .setTitle(request.getTitle())
                            .setBody(request.getMessage())
                            .build())
                    .build();
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            HttpStatus status = switch (e.getMessagingErrorCode()) {
                case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
                case UNREGISTERED -> HttpStatus.NOT_FOUND;
                case QUOTA_EXCEEDED -> HttpStatus.TOO_MANY_REQUESTS;
                case UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
                default -> HttpStatus.INTERNAL_SERVER_ERROR;
            };
            throw new FirebaseMessaginException(e.getMessage(), status);
        }
    }
}