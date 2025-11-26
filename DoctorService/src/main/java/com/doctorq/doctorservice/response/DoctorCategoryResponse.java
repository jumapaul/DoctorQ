package com.doctorq.doctorservice.response;

import java.util.List;

public record DoctorCategoryResponse(
        Long id,
        String name,
        String description,
        String categoryIcon,
        Integer doctorsCount,
        List<DoctorOverview> doctors
) {
}
