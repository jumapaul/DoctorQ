package com.doctorq.appointmentservice;

import com.doctorq.appointmentservice.dtos.AddAppointmentRequest;
import com.doctorq.appointmentservice.dtos.AppointmentResponse;
import com.doctorq.appointmentservice.dtos.AppointmentStatus;
import com.doctorq.appointmentservice.entity.AppointmentEntity;
import org.springframework.stereotype.Service;

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
                .time(request.time())
                .build();
    }

    public AppointmentResponse fromAppointmentEntity(AppointmentEntity appointmentEntity){
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
                appointmentEntity.getTime()
        );
    }
}
