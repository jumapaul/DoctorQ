package com.doctorq.userservice.favorite.favorite_mapper;

import com.doctorq.userservice.favorite.entity.FavoritesEntity;
import com.doctorq.userservice.favorite.response.DoctorOverview;
import com.doctorq.userservice.favorite.response.DoctorResponse;
import org.springframework.stereotype.Service;

@Service
public class FavoriteMapper {

    public DoctorResponse fromEntity(Long id, DoctorResponse response) {
        return new DoctorResponse(
                id,
                response.getDoctorId(),
                response.getFullName(),
                response.getEmail(),
                response.getHospital(),
                response.getProfilePictureUrl(),
                response.getRating(),
                response.getDoctorCategory(),
                response.getAboutDoctor(),
                response.getYearsOfExperience(),
                response.getNumberOfPatients(),
                response.getWorkingHours(),
                response.getReviewsCount()
        );
    }

    public DoctorOverview toDoctorOverView(Long id, DoctorResponse response){
        return new DoctorOverview(
                id,
                response.getDoctorId(),
                response.getFullName(),
                response.getEmail(),
                response.getHospital(),
                response.getProfilePictureUrl(),
                response.getRating(),
                response.getDoctorCategory(),
                response.getReviewsCount()
        );
    }
}
