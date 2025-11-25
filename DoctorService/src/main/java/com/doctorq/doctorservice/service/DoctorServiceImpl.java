package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.Roles;
import com.doctorq.doctorservice.response.PaginatedResponse;
import com.doctorq.doctorservice.utils.RedisUtil;
import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.dtos.DoctorResponse;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.exception.ConflictException;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.mapper.DoctorMapper;
import com.doctorq.doctorservice.repository.DoctorCategoryRepository;
import com.doctorq.doctorservice.repository.DoctorRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.doctorq.doctorservice.utils.Constants.*;
import static com.doctorq.doctorservice.utils.RedisRetrieveMethods.readCacheValue;
import static com.doctorq.doctorservice.utils.RedisRetrieveMethods.setCacheValue;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final RedisUtil redisUtil;
    private final DoctorRepository doctorRepository;
    private final DoctorCategoryRepository doctorCategoryRepository;
    private final DoctorMapper doctorMapper;

    @Transactional
    @Override
    public DoctorResponse addDoctor(DoctorRequest request) {
        redisUtil.delete(allDoctorsCache);
        redisUtil.delete(topDoctorsCache);
        Optional<DoctorEntity> existingDoctor = doctorRepository.findByEmail(request.email());

        if (existingDoctor.isPresent()) throw new ConflictException("Doctor already exists");


        Set<DoctorCategoryEntity> categoryEntities = request.doctorCategory().stream().map(categoryId ->
                doctorCategoryRepository.findById(categoryId).orElseThrow(() ->
                        new ResourceNotFoundException("Category with id not found")
                )
        ).collect(Collectors.toSet());

        DoctorEntity doctor = doctorMapper.toDoctorEntity(request);
        doctor.setDoctorCategory(categoryEntities);

        doctor = doctorRepository.save(doctor);

        categoryEntities.forEach(category ->
                doctorCategoryRepository.incrementDoctorCount(category.getId())
        );

        return doctorMapper.fromDoctorEntity(doctor);
    }

    @Override
    public PaginatedResponse<DoctorResponse> getAllDoctors(int page, int size) throws JsonProcessingException {
        Object doctorsCache = redisUtil.get(allDoctorsCache + page + size);

        if (doctorsCache == null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<DoctorEntity> doctors = doctorRepository.findAll(pageable);
            List<DoctorResponse> response = doctors.stream().map(doctorMapper::fromDoctorEntity).toList();

            PaginatedResponse<DoctorResponse> paginatedResponse = paginate(response, doctors);
            setCacheValue(redisUtil, allDoctorsCache + page + size, paginatedResponse);

            return paginatedResponse;
        }

        return readCacheValue(doctorsCache.toString(), new TypeReference<>() {
        });
    }

    @Override
    public DoctorResponse getDoctorById(Long id) throws JsonProcessingException {
        Object doctorCache = redisUtil.get(doctorByIdCache + id);

        if (doctorCache == null) {
            DoctorEntity doctor = doctorRepository.findById(id).orElseThrow(() ->
                    new ResourceNotFoundException("Doctor with id " + id + " not found")
            );

            DoctorResponse response = doctorMapper.fromDoctorEntity(doctor);

            setCacheValue(redisUtil, doctorByIdCache + id, response);
            return response;
        }
        return readCacheValue(doctorCache.toString(), new TypeReference<>() {
        });
    }

    @Override
    @Transactional
    public DoctorResponse updateDoctor(Long id, DoctorRequest request) throws JsonProcessingException {
        redisUtil.delete(allDoctorsCache);
        redisUtil.delete(topDoctorsCache);
        redisUtil.delete(doctorByIdCache + id);
        DoctorEntity doctor = doctorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Doctor with id " + id + " not found")
        );

        doctor.setFullName(request.fullName());
        doctor.setEmail(request.email());
        doctor.setHospital(request.hospital());
        doctor.setRating(doctor.getRating());
        doctor.setReviewsCount(doctor.getRatingCount());
        doctor.setRole(Roles.DOCTOR);
        doctor.setDoctorCategory(doctor.getDoctorCategory());
        doctor.setAboutDoctor(request.aboutDoctor());
        doctor.setWorkingHours(request.workingHours());
        doctor.setYearsOfExperience(request.yearsOfExperience());

        DoctorEntity savedDoctor = doctorRepository.save(doctor);
        DoctorResponse response = doctorMapper.fromDoctorEntity(savedDoctor);

        setCacheValue(redisUtil, doctorByIdCache + id, response);
        return response;
    }

    @Transactional
    @Override
    public void deleteDoctor(Long id) {
        redisUtil.delete(doctorByIdCache + id);
        redisUtil.delete(allDoctorsCache);
        doctorRepository.deleteById(id);
    }

    @Override
    public PaginatedResponse<DoctorResponse> getTopDoctors(int page, int size) throws JsonProcessingException {
        Object doctorsCache = redisUtil.get(topDoctorsCache + page + size);

        if (doctorsCache == null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<DoctorEntity> topDoctors = doctorRepository.getTopDoctor(pageable);
            List<DoctorResponse> response = topDoctors.stream().map(doctorMapper::fromDoctorEntity).toList();

            PaginatedResponse<DoctorResponse> paginatedResponse = paginate(response, topDoctors);
            setCacheValue(redisUtil, topDoctorsCache + page + size, paginatedResponse);
            return paginatedResponse;
        }
        return readCacheValue(doctorsCache.toString(), new TypeReference<>() {
        });
    }

    @Override
    public PaginatedResponse<DoctorResponse> getCategoryTopDoctor(int page, int size, Long categoryId) throws JsonProcessingException {
        Object topDoctorCache = redisUtil.get(topCategoryDoctorCache + page + size + categoryId);

        if (topDoctorCache == null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<DoctorEntity> topDoctors = doctorRepository.getCategoryTopDoctor(categoryId, pageable);
            List<DoctorResponse> response = topDoctors.stream().map(doctorMapper::fromDoctorEntity).toList();

            PaginatedResponse<DoctorResponse> paginatedResponse = paginate(response, topDoctors);

            setCacheValue(redisUtil, topCategoryDoctorCache + page + size + categoryId, paginatedResponse);

            return paginatedResponse;
        }
        return readCacheValue(topDoctorCache.toString(), new TypeReference<>() {
        });
    }

    @Override
    public PaginatedResponse<DoctorResponse> searchDoctorByName(String name, int page, int size) throws JsonProcessingException {
        Object searchedDoctor = redisUtil.get(searchedDoctorsCache + name + page + size);

        if (searchedDoctor == null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<DoctorEntity> searchedDoctors = doctorRepository.findAllByFullName(name, pageable);
            List<DoctorResponse> response = searchedDoctors.stream().map(doctorMapper::fromDoctorEntity).toList();

            PaginatedResponse<DoctorResponse> paginatedResponse = paginate(response, searchedDoctors);
            setCacheValue(redisUtil, searchedDoctorsCache + name + page + size, paginatedResponse);
            return paginatedResponse;
        }

        return readCacheValue(searchedDoctor.toString(), new TypeReference<>() {
        });
    }

    @Override
    public void updateRating(double rating, Long doctorId) throws JsonProcessingException {
        redisUtil.delete(allDoctorsCache);
        redisUtil.delete(topDoctorsCache);
        redisUtil.delete(doctorByIdCache + doctorId);

        DoctorEntity doctor = doctorRepository.findById(doctorId).orElseThrow(() ->
                new ResourceNotFoundException("Doctor not found")
        );

        doctor.setRating(rating);
        doctorRepository.save(doctor);
        DoctorResponse response = doctorMapper.fromDoctorEntity(doctor);
        setCacheValue(redisUtil, doctorByIdCache + doctorId, response);
    }

    //    @Override
//    public PaginatedResponse<DoctorResponse> getRecommendedDoctors(int page, int size) throws JsonProcessingException {
//        Object doctorsCache = redisUtil.get(recommendedDoctorCache);
//
//        if (doctorsCache == null) {
//            List<DoctorEntity> topDoctors = doctorRepository.getTopDoctor();
//            List<DoctorResponse> response = topDoctors.stream().map(doctorMapper::fromDoctorEntity).toList();
//            setCacheValue(redisUtil, topDoctorsCache, response);
//            return response;
//        }
//        return readCacheValue(doctorsCache.toString(), new TypeReference<>() {
//        });
//    }
//    public <T> PaginatedResponse<T> paginate(int page, int size, List<T> data) {


    private <T> PaginatedResponse<T> paginate(List<T> data, Page<?> paginatedData) {
        return new PaginatedResponse<>(
                data,
                paginatedData.getNumber(),
                paginatedData.getTotalPages(),
                paginatedData.getSize(),
                paginatedData.getNumberOfElements(),
                paginatedData.getSort().isSorted(),
                paginatedData.isLast()
        );
    }
}
