package com.doctorq.doctorservice.mapper;

import com.doctorq.doctorservice.dtos.DoctorCategoryRequest;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import org.springframework.stereotype.Service;

@Service
public class DoctorCategoryMapper {

    public DoctorCategoryEntity toDoctorsCategoryEntity(DoctorCategoryRequest request) {
        return DoctorCategoryEntity.builder()
                .name(request.name())
                .description(request.description())
                .build();
    }
}
