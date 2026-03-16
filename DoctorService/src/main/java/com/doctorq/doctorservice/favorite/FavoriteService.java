package com.doctorq.doctorservice.favorite;

import com.doctorq.doctorservice.dtos.response.DoctorResponse;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.mapper.DoctorMapper;
import com.doctorq.doctorservice.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    public String addToFavorite(FavoriteRequest request) {
        DoctorEntity doctor = doctorRepository.findById(request.doctorId()).orElseThrow(() ->
                new ResourceNotFoundException("Doctor not found")
        );

        UserFavoritesEntity entity = favoriteRepository.findByUserId(request.userId()).orElseGet(() ->
                UserFavoritesEntity.builder()
                        .userId(request.userId())
                        .favoriteDoctors(new HashSet<>())
                        .build()
        );
        entity.getFavoriteDoctors().add(doctor);

        favoriteRepository.save(entity);

        return "Doctor added to favorites";
    }

    public String removeFromFavorite(FavoriteRequest request) {
        UserFavoritesEntity entity = favoriteRepository.findByUserId(request.userId()).orElseThrow(() ->
                new ResourceNotFoundException("Doctor not found")
        );

        DoctorEntity doctor = doctorRepository.findById(request.doctorId()).orElseThrow(() ->
                new ResourceNotFoundException("Not found")
        );

        entity.getFavoriteDoctors().remove(doctor);

        favoriteRepository.save(entity);

        return "Doctor successfully removed from favorite";
    }

    public List<DoctorResponse> getUserFavorites(Long userId) {
        UserFavoritesEntity entity = favoriteRepository.findByUserId(userId).orElseThrow(() ->
                new ResourceNotFoundException("No favorites found")
        );

        return entity.getFavoriteDoctors().stream().map(doctorMapper::fromDoctorEntity).toList();
    }
}
