package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.dtos.DoctorResponse;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.response.ApiResponse;
import com.doctorq.doctorservice.response.PaginatedResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface DoctorService {
    DoctorResponse addDoctor(DoctorRequest request);

    PaginatedResponse<DoctorResponse> getAllDoctors(int page, int size) throws JsonProcessingException;

    PaginatedResponse<DoctorResponse> getTopDoctors(int page, int size) throws JsonProcessingException;

    PaginatedResponse<DoctorResponse> getCategoryTopDoctor(int page, int size, Long categoryId) throws JsonProcessingException;

    DoctorResponse getDoctorById(Long id) throws JsonProcessingException;

    DoctorResponse updateDoctor(Long id, DoctorRequest request) throws JsonProcessingException;

    void deleteDoctor(Long id);

    PaginatedResponse<DoctorResponse> searchDoctorByName(String name, int page, int size) throws JsonProcessingException;

    void updateRating(double rating, Long doctorId) throws JsonProcessingException;

//    PaginatedResponse<DoctorResponse> getRecommendedDoctors(int page, int size) throws JsonProcessingException;

}
