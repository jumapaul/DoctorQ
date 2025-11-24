package com.doctorq.doctorservice.mapper;

import com.doctorq.doctorservice.dtos.DoctorCategoryRequest;
import com.doctorq.doctorservice.dtos.DoctorCategoryResponse;
import com.doctorq.doctorservice.dtos.DoctorDto;
import com.doctorq.doctorservice.dtos.DoctorResponse;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import com.doctorq.doctorservice.entities.DoctorEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorCategoryMapper {

    public DoctorCategoryEntity toDoctorsCategoryEntity(DoctorCategoryRequest request) {
        return DoctorCategoryEntity.builder()
                .name(request.name())
                .description(request.description())
                .doctorsCount(0)
                .categoryIcon(request.categoryUrl())
                .build();
    }

    public DoctorDto fromEntity(DoctorEntity entity) {
        return new DoctorDto(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getProfilePictureUrl(),
                entity.getHospital(),
                entity.getRating(),
                entity.getRatingCount(),
                entity.getReviewsCount()
        );
    }
}
