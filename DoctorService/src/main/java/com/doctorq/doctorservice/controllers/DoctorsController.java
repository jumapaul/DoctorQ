package com.doctorq.doctorservice.controllers;

import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.entities.WorkingHours;
import com.doctorq.doctorservice.dtos.response.DoctorOverview;
import com.doctorq.doctorservice.dtos.response.DoctorResponse;
import com.doctorq.doctorservice.dtos.response.ApiResponse;
import com.doctorq.doctorservice.dtos.response.PaginatedResponse;
import com.doctorq.doctorservice.service.DoctorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/doctors")
@RequiredArgsConstructor
public class DoctorsController {

    private final DoctorService doctorService;

//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<DoctorResponse>> addDoctor(@RequestBody DoctorRequest request) {
//        DoctorResponse doctor = doctorService.addDoctor(request);
//
//        return ResponseEntity.ok(
//                success(doctor, "Doctor added successfully")
//        );
//    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<PaginatedResponse<DoctorOverview>> getAllDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "rate", defaultValue = "false") boolean sortByRating
    ) throws JsonProcessingException {
        return ResponseEntity.ok(doctorService.getAllDoctors(page, size, sortByRating));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorById(@PathVariable Long id) throws JsonProcessingException {
        DoctorResponse doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(
                success(doctor, "Doctor retrieved successfully")
        );
    }

    @PutMapping("/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateDoctor(
            @PathVariable Long doctorId, @RequestBody DoctorRequest request) throws JsonProcessingException {

        DoctorResponse doctor = doctorService.updateDoctor(doctorId, request);
        return ResponseEntity.ok(
                success(doctor, "Doctor updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<String>> deleteDoctor(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        doctorService.deleteDoctor(id, token);
        return ResponseEntity.ok(
                success(null, "Doctor deleted successfully")
        );
    }

    @GetMapping("/topRated")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<PaginatedResponse<DoctorOverview>> getTopDoctorResponse(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws JsonProcessingException {
        return ResponseEntity.ok(doctorService.getTopDoctors(page, size));
    }

    @GetMapping("/findDoctor")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<PaginatedResponse<DoctorOverview>> findDoctorByName(
            @Param("name") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws JsonProcessingException {

        return ResponseEntity.ok(doctorService.searchDoctorByName(name, page, size));
    }

    @GetMapping("/topRated/{categoryId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<PaginatedResponse<DoctorOverview>> getCategoryTopDoctor(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable(name = "categoryId") Long categoryId
    ) throws JsonProcessingException {
        return ResponseEntity.ok(doctorService.getCategoryTopDoctor(page, size, categoryId));
    }

    @PostMapping("updateSchedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateSchedule(
            @RequestBody WorkingHours workingHours
    ) {
        DoctorResponse response = doctorService.updateSchedule(workingHours);

        return ResponseEntity.ok(success(response, "Schedule update successfully"));
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
        return new ApiResponse<>(
                message,
                data
        );
    }
}
