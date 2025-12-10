package com.doctorq.appointmentservice.dtos;

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
        String date,
        String time
) {
}
