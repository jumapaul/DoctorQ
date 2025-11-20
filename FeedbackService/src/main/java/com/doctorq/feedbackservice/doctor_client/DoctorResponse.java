package com.doctorq.feedbackservice.doctor_client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DoctorResponse(
        Long id,
        String fullName,
        String email,
        String profilePictureUrl,
        String hospital,
        List<String> doctorCategory,
        Double rating,
        Integer ratingsCount,
        Integer reviewsCount
) {
}