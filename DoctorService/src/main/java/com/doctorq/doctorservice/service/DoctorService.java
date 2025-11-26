package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.response.DoctorOverview;
import com.doctorq.doctorservice.response.DoctorResponse;
import com.doctorq.doctorservice.response.PaginatedResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface DoctorService {
    DoctorResponse addDoctor(DoctorRequest request);

    PaginatedResponse<DoctorOverview> getAllDoctors(int page, int size) throws JsonProcessingException;

    PaginatedResponse<DoctorOverview> getTopDoctors(int page, int size) throws JsonProcessingException;

    PaginatedResponse<DoctorOverview> getCategoryTopDoctor(int page, int size, Long categoryId) throws JsonProcessingException;

    DoctorResponse getDoctorById(Long id) throws JsonProcessingException;

    DoctorResponse updateDoctor(Long id, DoctorRequest request) throws JsonProcessingException;

    void deleteDoctor(Long id);

    PaginatedResponse<DoctorOverview> searchDoctorByName(String name, int page, int size) throws JsonProcessingException;

    void updateRating(double rating, Long doctorId) throws JsonProcessingException;

//    PaginatedResponse<DoctorResponse> getRecommendedDoctors(int page, int size) throws JsonProcessingException;

}
