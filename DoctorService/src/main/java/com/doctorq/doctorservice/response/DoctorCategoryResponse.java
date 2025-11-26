package com.doctorq.doctorservice.response;

import com.doctorq.doctorservice.dtos.DoctorDto;

import java.util.List;

public record DoctorCategoryResponse(
        Long id,
        String name,
        String description,
        String categoryIcon,
        Integer doctorsCount,
        List<DoctorDto> doctors
) {
}
