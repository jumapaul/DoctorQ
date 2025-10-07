package com.doctorq.doctorservice.dtos;

import java.util.List;

public record DoctorRequest(
        String fullName,
        String email,
        String hospital,
        List<Long> doctorCategory
) {
}
