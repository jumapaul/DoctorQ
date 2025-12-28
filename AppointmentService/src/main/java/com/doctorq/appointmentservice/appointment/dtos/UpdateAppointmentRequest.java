package com.doctorq.appointmentservice.appointment.dtos;

public record UpdateAppointmentRequest(
        String username,
        String userMail,
        String doctorName,
        String doctorMail
) {
}
