package com.doctorq.userservice.favorite.response;

import java.util.List;

public record DoctorOverview(
        Long id,
        Long doctorId,
        String fullName,
        String email,
        String hospital,
        String profilePictureUrl,
        Double rating,
        List<String> doctorCategory,
        Integer reviewsCount
) {
}
