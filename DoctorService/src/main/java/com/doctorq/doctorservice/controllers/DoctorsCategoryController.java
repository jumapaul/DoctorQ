package com.doctorq.doctorservice.controllers;

import com.doctorq.doctorservice.dtos.DoctorCategoryRequest;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import com.doctorq.doctorservice.response.ApiResponse;
import com.doctorq.doctorservice.service.DoctorCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/category")
@RequiredArgsConstructor
public class DoctorsCategoryController {

    private final DoctorCategoryService doctorCategoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<DoctorCategoryEntity>> addCategory(
            @RequestBody DoctorCategoryRequest request) {
        return ResponseEntity.ok(doctorCategoryService.addCategory(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorCategoryEntity>>> getAllCategories() {
        return ResponseEntity.ok(doctorCategoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorCategoryEntity>> getCategoryById(
            @PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(doctorCategoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorCategoryEntity>> updateCategory(
            @PathVariable(name = "id") Long id,
            @RequestBody DoctorCategoryRequest request
    ) {
        return ResponseEntity.ok(doctorCategoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(
            @PathVariable(name = "id") Long id
    ) {
        return ResponseEntity.ok(doctorCategoryService.deleteCategory(id));
    }
}
