package com.doctorq.doctorservice.dtos.response;


public record WorkingHoursResponse(

        Long id,
        String date,
        String startTime,
        String endTime
) {
}
