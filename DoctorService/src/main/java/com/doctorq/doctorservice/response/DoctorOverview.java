package com.doctorq.doctorservice.response;

import java.util.List;

public record DoctorOverview(
        Long id,
        String fullName,
        String email,
        String profilePictureUrl,
        String hospital,
        List<String> doctorCategory,
        Double rating,
        Integer reviewsCount
) {
}
