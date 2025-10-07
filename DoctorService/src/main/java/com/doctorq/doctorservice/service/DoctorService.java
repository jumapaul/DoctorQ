package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.response.ApiResponse;

import java.util.List;

public interface DoctorService {
    ApiResponse<DoctorEntity> addDoctors(DoctorRequest request);

    ApiResponse<List<DoctorEntity>> getAllDoctors();

    ApiResponse<DoctorEntity> getDoctorById(Long id);

    ApiResponse<DoctorEntity> updateDoctor(Long id, DoctorRequest request);

    ApiResponse<String> deleteDoctor(Long id);
}
