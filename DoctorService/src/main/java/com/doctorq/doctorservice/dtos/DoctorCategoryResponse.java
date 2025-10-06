package com.doctorq.doctorservice.dtos;

public record DoctorCategoryResponse(
        Long id,
        String name,
        String description,
        String categoryIcon,
        Integer doctorsCount
) {
}
