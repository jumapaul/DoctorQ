package com.doctorq.doctorservice.dtos;

import java.util.List;

public record DoctorResponse(
        Long id,
        String fullName,
        String email,
        String profilePictureUrl,
        String hospital,
        List<String> specialization,
        Double rating,
        Roles role
) {
}
