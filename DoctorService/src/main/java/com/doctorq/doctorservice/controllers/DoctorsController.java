package com.doctorq.doctorservice.controllers;

import com.doctorq.doctorservice.dtos.DoctorRequest;
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
    public ResponseEntity<ApiResponse<DoctorEntity>> addDoctor(@RequestBody DoctorRequest request) {
        DoctorEntity doctor = doctorService.addDoctor(request);

        return ResponseEntity.ok(
                success(doctor)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorEntity>>> getAllDoctors() {
        List<DoctorEntity> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(
                success(doctors)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorEntity>> getDoctorById(@PathVariable Long id) {
        DoctorEntity doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(
                success(doctor)
        );
    }

    @PutMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<DoctorEntity>> updateDoctor(
            @PathVariable Long doctorId, @RequestBody DoctorRequest request) {

        DoctorEntity doctor = doctorService.updateDoctor(doctorId, request);
        return ResponseEntity.ok(
                success(doctor)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDoctor(
            @PathVariable Long id
    ) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok(
                success(null)
        );
    }

    private <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(
                HttpStatus.OK.value(),
                "Success",
                data
        );
    }
}
