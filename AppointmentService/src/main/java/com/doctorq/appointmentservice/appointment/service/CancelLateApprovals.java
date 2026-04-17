package com.doctorq.appointmentservice.appointment.service;

import com.doctorq.appointmentservice.appointment.dtos.AppointmentStatus;
import com.doctorq.appointmentservice.appointment.entity.AppointmentEntity;
import com.doctorq.appointmentservice.appointment.mappers.AppointmentMapper;
import com.doctorq.appointmentservice.appointment.repository.AppointmentRepository;
import com.doctorq.appointmentservice.exception.ResourceNotFoundException;
import com.doctorq.appointmentservice.notification.*;
import com.doctorq.appointmentservice.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.doctorq.appointmentservice.util.Constants.*;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class CancelLateApprovals {
    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;
    private final AppointmentMapper appointmentMapper;
    private final RedisUtil redisUtil;
    private final NotificationRepository notificationRepository;

    @Scheduled(fixedDelay = 600_000)
    public void pullAndUpdate() {
        appointmentRepository.findExpiredScheduledAppointments(
                LocalDate.now(), LocalTime.now(), AppointmentStatus.SCHEDULED).forEach(appointmentEntity -> {

            redisUtil.deleteGroup(userAppointmentByStatusCache);
            redisUtil.deleteGroup(doctorAppointmentByStatusCache);
            AppointmentEntity appointment = appointmentRepository.findById(appointmentEntity.getId()).orElseThrow(() ->
                    new ResourceNotFoundException("Appointment not found"));

            appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.save(appointment);
            appointmentMapper.fromAppointmentEntity(appointment);
        });
    }

    @Scheduled(fixedDelay = 1_800_000)
    public void sendReminder() {
        LocalTime now = LocalTime.now();
        appointmentRepository.findAppointmentsInNext30Minutes(
                LocalDate.now(), now, now.plusMinutes(30), AppointmentStatus.APPROVED
        ).forEach(appointmentEntity -> {
            String cacheKey = notificationByUserId + appointmentEntity.getUserId() + 0 + 10;
            NotificationRequest request = new NotificationRequest(
                    appointmentEntity.getUserId(),
                    "You have an appointment in 30 minutes",
                    "Appointment reminder"
            );
            NotificationEntity entity = NotificationEntity.builder()
                    .userId(request.getUserId())
                    .title(request.getTitle())
                    .description(request.getMessage())
                    .date(LocalDateTime.now())
                    .build();
            notificationRepository.save(entity);
            redisUtil.delete(cacheKey);
            notificationService.sendNotification(request);
        });
    }

}
