package com.doctorq.userservice.user.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

public record WorkingHours(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {
}
