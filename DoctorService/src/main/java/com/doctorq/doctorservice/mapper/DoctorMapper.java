package com.doctorq.doctorservice.mapper;

import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.dtos.Roles;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.repository.DoctorCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorMapper {
    private final DoctorCategoryRepository doctorCategoryRepository;

    public DoctorEntity toDoctorEntity(DoctorRequest request) {

        Set<DoctorCategoryEntity> categoryEntitySet = request.doctorCategory()
                .stream()
                .map(id -> doctorCategoryRepository.findById(id).orElseThrow(() ->
                        new ResourceNotFoundException("Category of the id " + id + " not found")
                )).collect(Collectors.toSet());

        return DoctorEntity.builder()
                .fullName(request.fullName())
                .email(request.email())
                .hospital(request.hospital())
                .role(Roles.DOCTOR)
                .doctorCategory(categoryEntitySet)
                .build();
    }
}
