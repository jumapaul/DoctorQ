package com.doctorq.appointmentservice.appointment.service;

import com.doctorq.appointmentservice.appointment.dtos.AppointmentStatus;
import com.doctorq.appointmentservice.appointment.repository.AppointmentRepository;
import com.doctorq.appointmentservice.notification.Notification;
import com.doctorq.appointmentservice.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.doctorq.appointmentservice.notification.NotificationType.CREATED;
import static com.doctorq.appointmentservice.notification.NotificationType.REMINDER;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class CancelLateApprovals {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;
    private final NotificationService notificationService;

    @Scheduled(fixedDelay = 600_000)
    public void pullAndUpdate() {
        appointmentRepository.findExpiredScheduledAppointments(
                LocalDate.now(), LocalTime.now(), AppointmentStatus.SCHEDULED).forEach(appointmentEntity -> {
            appointmentService.cancelAppointment(appointmentEntity.getId());
        });
    }

    @Scheduled(fixedDelay = 1_800_000)
    public void sendReminder() {

        LocalTime now = LocalTime.now();
        appointmentRepository.findAppointmentsInNext30Minutes(
                LocalDate.now(), now, now.plusMinutes(30), AppointmentStatus.APPROVED
        ).forEach(appointmentEntity -> {
            Notification notification = Notification.builder()
                    .message("You have an appoint in 30 minutes")
                    .title("Appointment reminder")
                    .type(REMINDER)
                    .timestamp(LocalDateTime.now())
                    .build();
            notificationService.sendNotification(appointmentEntity.getUserId(), notification);
        });
    }

}
