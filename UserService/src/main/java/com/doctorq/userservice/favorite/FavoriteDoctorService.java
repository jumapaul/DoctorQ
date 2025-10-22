package com.doctorq.userservice.favorite;

import com.doctorq.userservice.doctor_client.DoctorResponse;
import com.doctorq.userservice.user_profile.dtos.PaginatedResponse;

public interface FavoriteDoctorService {

    DoctorResponse addDoctor(FavoriteDoctorRequest request);

    PaginatedResponse<DoctorResponse> getAllUserFavoriteDoctors(Long id, int page, int size);

    void deleteFromFavoriteById(Long id);
}
