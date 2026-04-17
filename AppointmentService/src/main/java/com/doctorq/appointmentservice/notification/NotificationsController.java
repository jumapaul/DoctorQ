package com.doctorq.appointmentservice.notification;

import com.doctorq.appointmentservice.appointment.dtos.PaginatedResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/notifications")
@RequiredArgsConstructor
public class NotificationsController {
    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<PaginatedResponse<NotificationEntity>> getNotificationsByUserId(
            @PathVariable Long userId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) throws JsonProcessingException {
        return ResponseEntity.ok(notificationService.getAllNotifications(userId, page, size));
    }
}
