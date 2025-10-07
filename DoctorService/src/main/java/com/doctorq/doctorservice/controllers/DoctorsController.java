package com.doctorq.doctorservice.controllers;

import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.response.ApiResponse;
import com.doctorq.doctorservice.service.DoctorService;
import lombok.RequiredArgsConstructor;
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
        return ResponseEntity.ok(doctorService.addDoctors(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorEntity>>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorEntity>> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PutMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<DoctorEntity>> updateDoctor(
            @PathVariable Long doctorId, @RequestBody DoctorRequest request) {
        return ResponseEntity.ok(doctorService.updateDoctor(doctorId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDoctor(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(doctorService.deleteDoctor(id));
    }

}
