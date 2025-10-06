package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.response.ApiResponse;

import java.util.List;

public interface DoctorService {
    DoctorEntity addDoctor(DoctorRequest request);

    List<DoctorEntity> getAllDoctors();

    DoctorEntity getDoctorById(Long id);

    DoctorEntity updateDoctor(Long id, DoctorRequest request);

    void deleteDoctor(Long id);
}
