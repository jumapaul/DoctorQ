package com.doctorq.appointmentservice.appointment.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentResponse(
        Long id,
        String patientName,
        Integer patientAge,
        String patientContact,
        String patientGender,
        String patientDescription,
        Long doctorId,
        Long userId,
        AppointmentStatus appointmentStatus,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime

) {
}
