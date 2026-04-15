package com.doctorq.appointmentservice.notification;

import com.doctorq.appointmentservice.appointment.dtos.AppointmentResponse;

public record NotificationEvent(
        AppointmentResponse response,
        String desc
) {
}
