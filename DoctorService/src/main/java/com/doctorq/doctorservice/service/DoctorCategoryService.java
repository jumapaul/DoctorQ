package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.DoctorCategoryRequest;
import com.doctorq.doctorservice.dtos.DoctorCategoryResponse;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface DoctorCategoryService {

    DoctorCategoryEntity addCategory(DoctorCategoryRequest request);

    List<DoctorCategoryEntity> getAllCategories() throws JsonProcessingException;

    DoctorCategoryResponse getCategoryById(Long id) throws JsonProcessingException;

    DoctorCategoryResponse updateCategory(Long id, DoctorCategoryRequest request) throws JsonProcessingException;

    void deleteCategory(Long id);

}
