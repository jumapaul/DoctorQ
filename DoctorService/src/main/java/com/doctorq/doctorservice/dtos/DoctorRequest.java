package com.doctorq.doctorservice.dtos;

import java.util.List;

public record DoctorRequest(
        Long userId,
        String fullName,
        String email,
        String profilePicUrl,
        String hospital,
        List<Long> doctorCategory,
        String aboutDoctor,
        Integer yearsOfExperience,
        WorkingHoursRequest schedule
) {
}
