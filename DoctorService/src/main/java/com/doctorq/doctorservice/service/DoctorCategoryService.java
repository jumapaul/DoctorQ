package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.DoctorCategoryRequest;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import com.doctorq.doctorservice.response.ApiResponse;

import java.util.List;

public interface DoctorCategoryService {

    ApiResponse<DoctorCategoryEntity> addCategory(DoctorCategoryRequest request);

    ApiResponse<List<DoctorCategoryEntity>> getAllCategories();

    ApiResponse<DoctorCategoryEntity> getCategoryById(Long id);

    ApiResponse<DoctorCategoryEntity> updateCategory(Long id, DoctorCategoryRequest request);

    ApiResponse<String> deleteCategory(Long id);

}
