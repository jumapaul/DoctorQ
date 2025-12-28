package com.doctorq.appointmentservice.appointment.mappers;

import com.doctorq.appointmentservice.appointment.dtos.*;
import com.doctorq.appointmentservice.appointment.entity.AppointmentEntity;
import com.doctorq.appointmentservice.appointment.entity.HistoryOuterBoxEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentMapper {

    public AppointmentEntity toAppointmentEntity(AddAppointmentRequest request) {
        return AppointmentEntity.builder()
                .patientName(request.patientName())
                .patientAge(request.patientAge())
                .patientContact(request.patientContact())
                .patientDescription(request.patientDescription())
                .patientGender(request.patientGender())
                .doctorId(request.doctorId())
                .userId(request.userId())
                .appointmentStatus(AppointmentStatus.SCHEDULED)
                .date(request.date())
                .starTime(request.startTime())
                .endTime(request.endTime())
                .build();
    }

    public AppointmentResponse fromAppointmentEntity(AppointmentEntity appointmentEntity) {
        return new AppointmentResponse(
                appointmentEntity.getId(),
                appointmentEntity.getPatientName(),
                appointmentEntity.getPatientAge(),
                appointmentEntity.getPatientContact(),
                appointmentEntity.getPatientGender(),
                appointmentEntity.getPatientDescription(),
                appointmentEntity.getDoctorId(),
                appointmentEntity.getUserId(),
                appointmentEntity.getAppointmentStatus(),
                appointmentEntity.getDate(),
                appointmentEntity.getStarTime(),
                appointmentEntity.getEndTime()
        );
    }
}
