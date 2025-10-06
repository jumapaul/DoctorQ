package com.doctorq.doctorservice.dtos;

public record DoctorCategoryRequest(
        String name,
        String description,
        String categoryUrl
) {
}
