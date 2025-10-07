package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.DoctorCategoryRequest;
import com.doctorq.doctorservice.dtos.DoctorCategoryResponse;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;

import java.util.List;

public interface DoctorCategoryService {

    DoctorCategoryEntity addCategory(DoctorCategoryRequest request);

    List<DoctorCategoryEntity> getAllCategories();

    DoctorCategoryResponse getCategoryById(Long id);

    DoctorCategoryResponse updateCategory(Long id, DoctorCategoryRequest request);

    void deleteCategory(Long id);

}
