package com.doctorq.doctorservice.favorite;

import com.doctorq.doctorservice.dtos.response.DoctorResponse;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.mapper.DoctorMapper;
import com.doctorq.doctorservice.repository.DoctorRepository;
import com.doctorq.doctorservice.utils.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.doctorq.doctorservice.utils.Constants.userFavoriteDoctors;
import static com.doctorq.doctorservice.utils.RedisReadWriteMethods.*;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final RedisUtil redisUtil;

    public String addToFavorite(FavoriteRequest request) {
        String cacheKey = userFavoriteDoctors + request.userId();
        redisUtil.delete(cacheKey);
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
        String cacheKey = userFavoriteDoctors + request.userId();
        redisUtil.delete(cacheKey);
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

    public List<DoctorResponse> getUserFavorites(Long userId) throws JsonProcessingException {
        String cacheKey = userFavoriteDoctors + userId;
        Object cachedData = redisUtil.get(cacheKey);

        if (cachedData != null) return readCacheValue(cachedData, new TypeReference<>() {
        });

        Optional<UserFavoritesEntity> entity = favoriteRepository.findByUserId(userId);

        if (entity.isEmpty()) return Collections.emptyList();

        List<DoctorResponse> doctorResponses = entity.get().getFavoriteDoctors().stream().map(doctorMapper::fromDoctorEntity).toList();

        setGroupCacheValue(redisUtil, cacheKey, doctorResponses);
        return doctorResponses;
    }
}
