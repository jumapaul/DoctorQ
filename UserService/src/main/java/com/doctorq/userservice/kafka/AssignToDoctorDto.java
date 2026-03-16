package com.doctorq.userservice.kafka;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AssignToDoctorDto(
        Long userId,
        String hospital,
        String about,
        Integer yearsOfExperience,
        List<Long> categories,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {
}
