package com.doctorq.doctorservice.dtos;

public record DoctorRequest(
        String fullName,
        String email,
        Specialization specialization,
        String hospital
) {
}
