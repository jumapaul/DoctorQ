package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.DoctorCategoryRequest;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import com.doctorq.doctorservice.exception.ConflictException;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.mapper.DoctorCategoryMapper;
import com.doctorq.doctorservice.repository.DoctorCategoryRepository;
import com.doctorq.doctorservice.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorCategoryServiceImpl implements DoctorCategoryService {

    private final DoctorCategoryRepository doctorCategoryRepository;
    private final DoctorCategoryMapper doctorCategoryMapper;

    @Override
    public ApiResponse<DoctorCategoryEntity> addCategory(DoctorCategoryRequest request) {

        if (doctorCategoryRepository.findByName(request.name()).isPresent())
            throw new ConflictException("Category already added");

        DoctorCategoryEntity categoryEntity = doctorCategoryRepository.save(doctorCategoryMapper.toDoctorsCategoryEntity(request));
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Category added successfully",
                categoryEntity
        );
    }

    @Override
    public ApiResponse<List<DoctorCategoryEntity>> getAllCategories() {
        List<DoctorCategoryEntity> allCategories = doctorCategoryRepository.findAll();

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Categories retrieved successfully",
                allCategories
        );
    }

    @Override
    public ApiResponse<DoctorCategoryEntity> getCategoryById(Long id) {
        DoctorCategoryEntity doctorCategoryEntity = doctorCategoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category not found")
        );
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Category retrieved successfully",
                doctorCategoryEntity
        );
    }

    @Override
    public ApiResponse<DoctorCategoryEntity> updateCategory(Long id, DoctorCategoryRequest request) {
        DoctorCategoryEntity doctorCategoryEntity = doctorCategoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category with id " + id + " not found")
        );

        doctorCategoryEntity.setName(request.name());
        doctorCategoryEntity.setDescription(request.description());

        doctorCategoryRepository.save(doctorCategoryEntity);
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Category updated successfully",
                doctorCategoryEntity
        );
    }

    @Override
    public ApiResponse<String> deleteCategory(Long id) {
        doctorCategoryRepository.deleteById(id);
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Category successfully deleted",
                null
        );
    }
}
