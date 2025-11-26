package com.doctorq.doctorservice.controllers;

import com.doctorq.doctorservice.dtos.DoctorCategoryRequest;
import com.doctorq.doctorservice.response.DoctorCategoryResponse;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import com.doctorq.doctorservice.response.ApiResponse;
import com.doctorq.doctorservice.service.DoctorCategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/doctors/category")
@RequiredArgsConstructor
public class DoctorsCategoryController {

    private final DoctorCategoryService doctorCategoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<DoctorCategoryEntity>> addCategory(
            @RequestBody DoctorCategoryRequest request) {

        DoctorCategoryEntity doctorCategory = doctorCategoryService.addCategory(request);
        return ResponseEntity.ok(response(doctorCategory));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorCategoryEntity>>> getAllCategories() throws JsonProcessingException {

        List<DoctorCategoryEntity> categoryResponseList = doctorCategoryService.getAllCategories();
        return ResponseEntity.ok(response(categoryResponseList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorCategoryResponse>> getCategoryById(
            @PathVariable(name = "id") Long id) throws JsonProcessingException {
        DoctorCategoryResponse doctorCategory = doctorCategoryService.getCategoryById(id);
        return ResponseEntity.ok(response(doctorCategory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorCategoryResponse>> updateCategory(
            @PathVariable(name = "id") Long id,
            @RequestBody DoctorCategoryRequest request
    ) throws JsonProcessingException {
        DoctorCategoryResponse doctorCategory = doctorCategoryService.updateCategory(id, request);
        return ResponseEntity.ok(response(doctorCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable(name = "id") Long id
    ) {
        doctorCategoryService.deleteCategory(id);
        return ResponseEntity.ok(response(null));
    }

    private <T> ApiResponse<T> response(T data) {
        return new ApiResponse<T>(
                HttpStatus.OK.value(),
                "Success",
                data
        );
    }
}
