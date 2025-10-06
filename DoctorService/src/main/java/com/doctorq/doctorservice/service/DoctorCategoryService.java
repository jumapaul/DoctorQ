package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.DoctorCategoryRequest;
import com.doctorq.doctorservice.dtos.DoctorCategoryResponse;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import com.doctorq.doctorservice.response.ApiResponse;

import java.util.List;

public interface DoctorCategoryService {

    DoctorCategoryEntity addCategory(DoctorCategoryRequest request);

    List<DoctorCategoryResponse> getAllCategories();

    DoctorCategoryEntity getCategoryById(Long id);

    DoctorCategoryEntity updateCategory(Long id, DoctorCategoryRequest request);

    void deleteCategory(Long id);

}
