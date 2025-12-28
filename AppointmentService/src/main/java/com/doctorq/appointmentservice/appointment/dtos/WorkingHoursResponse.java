package com.doctorq.appointmentservice.appointment.dtos;


public record WorkingHoursResponse(

        Long id,
        String date,
        String startTime,
        String endTime
) {
}
