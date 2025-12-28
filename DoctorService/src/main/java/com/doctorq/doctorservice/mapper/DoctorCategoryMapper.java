package com.doctorq.doctorservice.mapper;

import com.doctorq.doctorservice.dtos.DoctorCategoryRequest;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.dtos.response.DoctorOverview;
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

    public DoctorOverview fromEntity(DoctorEntity entity) {
        List<String> categories = entity.getDoctorCategory().stream().map(DoctorCategoryEntity::getName).toList();

        return new DoctorOverview(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getProfilePictureUrl(),
                entity.getHospital(),
                categories,
                entity.getRating(),
                entity.getReviewsCount()
        );
    }
}
