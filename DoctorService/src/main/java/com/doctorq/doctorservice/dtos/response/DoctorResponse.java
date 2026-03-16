package com.doctorq.doctorservice.dtos.response;

import com.doctorq.doctorservice.dtos.Roles;
import com.doctorq.doctorservice.entities.WorkingHours;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public record DoctorResponse(
        Long id,
        Long userId,
        String fullName,
        String email,
        String profilePictureUrl,
        String hospital,
        List<String> doctorCategory,
        Double rating,
        Integer ratingsCount,
        Integer reviewsCount,
        String aboutDoctor,
        Integer yearsOfExperience,
        Integer numberOfPatients,
        WorkingHoursResponse schedule
) {
}
