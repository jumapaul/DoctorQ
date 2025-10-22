package com.doctorq.doctorservice.controllers;

import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.dtos.DoctorResponse;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.response.ApiResponse;
import com.doctorq.doctorservice.service.DoctorService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getAllDoctors() {
        List<DoctorResponse> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(
                success(doctors, "All doctors retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorById(@PathVariable Long id) {
        DoctorResponse doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(
                success(doctor, "Doctor retrieved successfully")
        );
    }

    @PutMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateDoctor(
            @PathVariable Long doctorId, @RequestBody DoctorRequest request) {

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
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getTopDoctorResponse() {
        List<DoctorResponse> topDoctors = doctorService.getTopDoctors();

        return ResponseEntity.ok(
                success(topDoctors, "Top rated doctors retrieved successfully")
        );
    }

    private <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<T>(
                HttpStatus.OK.value(),
                message,
                data
        );
    }
}
