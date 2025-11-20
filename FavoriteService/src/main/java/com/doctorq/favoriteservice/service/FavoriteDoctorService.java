package com.doctorq.favoriteservice.service;

import com.doctorq.favoriteservice.dtos.FavoriteDoctorRequest;
import com.doctorq.favoriteservice.response.DoctorResponse;
import com.doctorq.favoriteservice.response.PaginatedResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface FavoriteDoctorService {

    DoctorResponse addDoctor(FavoriteDoctorRequest request);

    PaginatedResponse<DoctorResponse> getAllUserFavoriteDoctors(Long id, int page, int size) throws JsonProcessingException;

    void deleteFromFavoriteById(Long id);
}
