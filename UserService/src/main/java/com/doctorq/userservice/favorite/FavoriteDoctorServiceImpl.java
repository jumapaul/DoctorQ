package com.doctorq.userservice.favorite;

import com.doctorq.userservice.doctor_client.DoctorClient;
import com.doctorq.userservice.doctor_client.DoctorResponse;
import com.doctorq.userservice.exception.ConflictException;
import com.doctorq.userservice.exception.ServiceUnavailableException;
import com.doctorq.userservice.response.ApiResponse;
import com.doctorq.userservice.user.repository.UserRepository;
import com.doctorq.userservice.user_profile.dtos.PaginatedResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteDoctorServiceImpl implements FavoriteDoctorService {

    private final FavoriteDoctorRepository favoriteRepository;
    private final UserRepository userRepository;
    private final DoctorClient doctorClient;

    @Override
    @CacheEvict(value = "getAllUserFavoriteDoctors", allEntries = true)
    public DoctorResponse addDoctor(FavoriteDoctorRequest request) {

        if (favoriteRepository.existsByUserIdAndDoctorId(request.userId(), request.doctorId()))
            throw new ConflictException("User already added to favorites");
        ApiResponse<DoctorResponse> doctorResponse = getDoctorById(request);

        userRepository.findById(request.userId()).orElseThrow(() ->
                new UsernameNotFoundException("User not found")
        );

        FavoritesEntity favoritesEntity = FavoritesEntity.builder()
                .userId(request.userId())
                .doctorId(request.doctorId())
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.save(favoritesEntity);
        return new DoctorResponse(
                doctorResponse.getData().getId(),
                doctorResponse.getData().getFullName(),
                doctorResponse.getData().getEmail(),
                doctorResponse.getData().getHospital()
        );
    }

    @CircuitBreaker(name = "doctorCircuitBreaker", fallbackMethod = "doctorFallback")
    private ApiResponse<DoctorResponse> getDoctorById(FavoriteDoctorRequest request) {
        return doctorClient.getDoctorById(request.doctorId());
    }

    @CacheEvict(value = "getAllUserFavoriteDoctors", allEntries = true)
    @Override
    public void deleteFromFavoriteById(Long id) {
        favoriteRepository.deleteById(id);
    }

    @Cacheable(value = "getAllUserFavoriteDoctors")
    @Override
    public PaginatedResponse<DoctorResponse> getAllUserFavoriteDoctors(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FavoritesEntity> paginatedFavorites = favoriteRepository.findAllByUserId(id, pageable);

        List<DoctorResponse> responseList = paginatedFavorites.getContent()
                .stream()
                .map(doctor -> {
                    DoctorResponse response = doctorClient.getDoctorById(doctor.getDoctorId()).getData();

                    return new DoctorResponse(
                            response.getId(),
                            response.getFullName(),
                            response.getEmail(),
                            response.getHospital()
                    );
                }).toList();

        return new PaginatedResponse<>(
                responseList,
                paginatedFavorites.getNumber(),
                paginatedFavorites.getTotalPages(),
                paginatedFavorites.getSize(),
                paginatedFavorites.getNumberOfElements(),
                paginatedFavorites.getSort().isSorted(),
                paginatedFavorites.isLast()
        );
    }

    private DoctorResponse doctorFallback(FavoriteDoctorRequest request, Exception ex) {
        log.error("Circuit breaker activated: {}", ex.getMessage());
        throw new ServiceUnavailableException(
                "Doctor service is temporarily unavailable. Please try again later"
        );
    }
}
