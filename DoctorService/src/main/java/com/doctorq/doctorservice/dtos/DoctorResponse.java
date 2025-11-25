package com.doctorq.doctorservice.dtos;

import java.util.List;

public record DoctorResponse(
        Long id,
        String fullName,
        String email,
        String profilePictureUrl,
        String hospital,
        List<String> doctorCategory,
        Double rating,
        Roles role,
        Integer ratingsCount,
        Integer reviewsCount,
        String aboutDoctor,
        Integer yearsOfExperience,
        Integer numberOfPatients,
        String workingHours
) {
}
