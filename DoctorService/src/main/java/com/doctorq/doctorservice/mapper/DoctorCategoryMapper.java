package com.doctorq.doctorservice.mapper;

import com.doctorq.doctorservice.dtos.DoctorCategoryRequest;
import com.doctorq.doctorservice.dtos.DoctorCategoryResponse;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import org.springframework.stereotype.Service;

@Service
public class DoctorCategoryMapper {

    public DoctorCategoryEntity toDoctorsCategoryEntity(DoctorCategoryRequest request) {
        return DoctorCategoryEntity.builder()
                .name(request.name())
                .description(request.description())
                .categoryIcon(request.categoryUrl())
                .build();
    }

    public DoctorCategoryResponse fromDoctorEntity(DoctorCategoryEntity entity) {
        return new DoctorCategoryResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCategoryIcon(),
                entity.getDoctors().size()
        );
    }
}
