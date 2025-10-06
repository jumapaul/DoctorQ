package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.exception.ConflictException;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.mapper.DoctorMapper;
import com.doctorq.doctorservice.repository.DoctorCategoryRepository;
import com.doctorq.doctorservice.repository.DoctorRepository;
import com.doctorq.doctorservice.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorCategoryRepository doctorCategoryRepository;
    private final DoctorMapper doctorMapper;

    @Transactional
    @Override
    public DoctorEntity addDoctor(DoctorRequest request) {

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

        return doctor;
    }

    @Cacheable(value = "allDoctors")
    @Override
    public List<DoctorEntity> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Cacheable(key = "#id", value = "DoctorEntity")
    @Override
    public DoctorEntity getDoctorById(Long id) {

        return doctorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Doctor with id " + id + " not found")
        );
    }

    @CacheEvict(value = "DoctorEntity", key = "#id")
    @Override
    public DoctorEntity updateDoctor(Long id, DoctorRequest request) {
        DoctorEntity doctor = doctorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Doctor with id " + id + " not found")
        );

        doctor.setFullName(request.fullName());
        doctor.setEmail(request.email());
        doctor.setHospital(request.hospital());

        return doctorRepository.save(doctor);
    }


    @Caching(
            evict = {
                    @CacheEvict(value = "DoctorEntity", key = "#id"),
                    @CacheEvict(value = "allDoctors", allEntries = true)
            }
    )
    @Override
    public void deleteDoctor(Long id) {
         doctorRepository.deleteById(id);
    }
}
