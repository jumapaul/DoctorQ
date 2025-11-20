package com.doctorq.doctorservice.controllers;

import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.dtos.DoctorResponse;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.response.ApiResponse;
import com.doctorq.doctorservice.response.PaginatedResponse;
import com.doctorq.doctorservice.service.DoctorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/doctors")
@RequiredArgsConstructor
public class DoctorsController {

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<ApiResponse<DoctorResponse>> addDoctor(@RequestBody DoctorRequest request) {
        DoctorResponse doctor = doctorService.addDoctor(request);

        return ResponseEntity.ok(
                success(doctor, "Doctor added successfully")
        );
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<DoctorResponse>> getAllDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws JsonProcessingException {
        return ResponseEntity.ok(doctorService.getAllDoctors(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorById(@PathVariable Long id) throws JsonProcessingException {
        DoctorResponse doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(
                success(doctor, "Doctor retrieved successfully")
        );
    }

    @PutMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateDoctor(
            @PathVariable Long doctorId, @RequestBody DoctorRequest request) throws JsonProcessingException {

        DoctorResponse doctor = doctorService.updateDoctor(doctorId, request);
        return ResponseEntity.ok(
                success(doctor, "Doctor updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDoctor(
            @PathVariable Long id
    ) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok(
                success(null, "Doctor deleted successfully")
        );
    }

    @GetMapping("/topRated")
    public ResponseEntity<PaginatedResponse<DoctorResponse>> getTopDoctorResponse(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws JsonProcessingException {
        return ResponseEntity.ok(doctorService.getTopDoctors(page, size));
    }

    @GetMapping("/findDoctor")
    public ResponseEntity<PaginatedResponse<DoctorResponse>> findDoctorByName(
            @Param("name") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws JsonProcessingException {

        return ResponseEntity.ok(doctorService.searchDoctorByName(name, page, size));
    }

    @GetMapping("/topRated/{categoryId}")
    public ResponseEntity<PaginatedResponse<DoctorResponse>> getCategoryTopDoctor(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable(name = "categoryId") Long categoryId
    ) throws JsonProcessingException {
        return ResponseEntity.ok(doctorService.getCategoryTopDoctor(page, size, categoryId));
    }
//
//    @GetMapping("/recommendation")
//    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getDoctorRecommendation() throws JsonProcessingException {
//        List<DoctorResponse> response = doctorService.getRecommendedDoctors();
//
//        return ResponseEntity.ok(
//                success(response, "Recommended doctors found")
//        );
//    }

    private <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<T>(
                HttpStatus.OK.value(),
                message,
                data
        );
    }
}
