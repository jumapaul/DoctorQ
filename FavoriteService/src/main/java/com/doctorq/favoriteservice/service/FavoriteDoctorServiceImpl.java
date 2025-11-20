package com.doctorq.favoriteservice.service;

import com.doctorq.favoriteservice.doctor_client.DoctorClient;
import com.doctorq.favoriteservice.exception.ConflictException;
import com.doctorq.favoriteservice.exception.ServiceUnavailableException;
import com.doctorq.favoriteservice.repository.FavoriteDoctorRepository;
import com.doctorq.favoriteservice.dtos.FavoriteDoctorRequest;
import com.doctorq.favoriteservice.entity.FavoritesEntity;
import com.doctorq.favoriteservice.response.ApiResponse;
import com.doctorq.favoriteservice.response.DoctorResponse;
import com.doctorq.favoriteservice.response.PaginatedResponse;
import com.doctorq.favoriteservice.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

import static com.doctorq.favoriteservice.util.Constants.GetAllUserFavoriteDoctors;
import static com.doctorq.favoriteservice.util.RedisRetrieveMethods.readCacheValue;
import static com.doctorq.favoriteservice.util.RedisRetrieveMethods.setCacheValue;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteDoctorServiceImpl implements FavoriteDoctorService {

    private final FavoriteDoctorRepository favoriteRepository;
    private final DoctorClient doctorClient;
    private final RedisUtil redisUtil;

    @Override
    public DoctorResponse addDoctor(FavoriteDoctorRequest request) {
        redisUtil.delete(GetAllUserFavoriteDoctors);
        if (favoriteRepository.existsByUserIdAndDoctorId(request.userId(), request.doctorId()))
            throw new ConflictException("User already added to favorites");
        ApiResponse<DoctorResponse> doctorResponse = getDoctorById(request);

        FavoritesEntity favoritesEntity = FavoritesEntity.builder()
                .userId(request.userId())
                .doctorId(request.doctorId())
                .createdAt(OffsetDateTime.now())
                .build();

        favoriteRepository.save(favoritesEntity);

        return new DoctorResponse(
                favoritesEntity.getId(),
                doctorResponse.getData().getFullName(),
                doctorResponse.getData().getEmail(),
                doctorResponse.getData().getHospital(),
                doctorResponse.getData().getProfilePictureUrl(),
                doctorResponse.getData().getRating(),
                doctorResponse.getData().getDoctorCategory()
        );
    }

    @CircuitBreaker(name = "doctorCircuitBreaker", fallbackMethod = "doctorFallback")
    private ApiResponse<DoctorResponse> getDoctorById(FavoriteDoctorRequest request) {
        return doctorClient.getDoctorById(request.doctorId());
    }

    @Override
    public void deleteFromFavoriteById(Long id) {
        redisUtil.delete(GetAllUserFavoriteDoctors);
        favoriteRepository.deleteById(id);
    }

    @Cacheable(value = "getAllUserFavoriteDoctors")
    @Override
    public PaginatedResponse<DoctorResponse> getAllUserFavoriteDoctors(Long id, int page, int size) throws JsonProcessingException {
        Object favoriteDoctorCache = redisUtil.get(GetAllUserFavoriteDoctors + page + size);

        if (favoriteDoctorCache == null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<FavoritesEntity> paginatedFavorites = favoriteRepository.findAllByUserId(id, pageable);
            List<DoctorResponse> responseList = paginatedFavorites.getContent()
                    .stream()
                    .map(favoriteDoctor -> {
                        DoctorResponse response = doctorClient.getDoctorById(favoriteDoctor.getDoctorId()).getData();

                        return new DoctorResponse(
                                favoriteDoctor.getId(),
                                response.getFullName(),
                                response.getEmail(),
                                response.getHospital(),
                                response.getProfilePictureUrl(),
                                response.getRating(),
                                response.getDoctorCategory()
                        );
                    }).toList();

            PaginatedResponse<DoctorResponse> paginatedResponse = new PaginatedResponse<>(
                    responseList,
                    paginatedFavorites.getNumber(),
                    paginatedFavorites.getTotalPages(),
                    paginatedFavorites.getSize(),
                    paginatedFavorites.getNumberOfElements(),
                    paginatedFavorites.getSort().isSorted(),
                    paginatedFavorites.isLast()
            );

            setCacheValue(redisUtil, GetAllUserFavoriteDoctors + page + size, paginatedResponse);

            return paginatedResponse;
        }

        return readCacheValue(favoriteDoctorCache.toString(), new TypeReference<>() {
        });
    }

    private DoctorResponse doctorFallback(FavoriteDoctorRequest request, Exception ex) {
        log.error("Circuit breaker activated: {}", ex.getMessage());
        throw new ServiceUnavailableException(
                "Doctor service is temporarily unavailable. Please try again later"
        );
    }
}
