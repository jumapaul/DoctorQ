package com.doctorq.userservice.favorite.service;

import com.doctorq.userservice.favorite.dtos.FavoriteDoctorRequest;
import com.doctorq.userservice.favorite.response.DoctorResponse;
import com.doctorq.userservice.response.PaginatedResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface FavoriteDoctorService {

    DoctorResponse addDoctor(FavoriteDoctorRequest request);

    PaginatedResponse<DoctorResponse> getAllUserFavoriteDoctors(Long id, int page, int size) throws JsonProcessingException;

    void deleteFromFavoriteById(Long id);
}
