package com.doctorq.doctorservice.dtos;

import java.time.LocalDate;
import java.time.LocalTime;

public record WorkingHoursRequest(
         LocalDate date,
         LocalTime startTime,
         LocalTime endTime
) {
}
