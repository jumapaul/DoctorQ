package com.doctorq.doctorservice.dtos;

public record DoctorDto(
        Long id,
        String fullName,
        String email,
        String profilePictureUrl,
        String hospital,
        Double rating
) {
}
