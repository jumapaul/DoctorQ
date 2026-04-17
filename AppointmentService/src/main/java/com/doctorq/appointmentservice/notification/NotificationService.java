package com.doctorq.appointmentservice.notification;

import com.doctorq.appointmentservice.appointment.dtos.PaginatedResponse;
import com.doctorq.appointmentservice.exception.FirebaseMessaginException;
import com.doctorq.appointmentservice.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.doctorq.appointmentservice.util.Constants.notificationByUserId;
import static com.doctorq.appointmentservice.util.RedisRetrieveMethods.readCacheValue;
import static com.doctorq.appointmentservice.util.RedisRetrieveMethods.setCacheValue;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final RedisUtil redisUtil;

    public PaginatedResponse<NotificationEntity> getAllNotifications(Long userId, int page, int size) throws JsonProcessingException {
        String cacheKey = notificationByUserId + userId + page + size;
        Object cachedData = redisUtil.get(cacheKey);

        if (cachedData != null) return readCacheValue(cachedData.toString(), new TypeReference<>() {
        });

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "date");

        Page<NotificationEntity> notifications = notificationRepository
                .findByUserId(userId, pageable);

        setCacheValue(redisUtil, cacheKey, notifications);
        return new PaginatedResponse<>(
                notifications.stream().toList(),
                notifications.getNumber(),
                notifications.getTotalPages(),
                notifications.getSize(),
                notifications.getNumberOfElements(),
                notifications.getSort().isSorted(),
                notifications.isLast()
        );
    }

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