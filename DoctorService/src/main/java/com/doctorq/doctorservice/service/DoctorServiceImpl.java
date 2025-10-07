package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.dtos.DoctorResponse;
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

    @Caching(evict = {
            @CacheEvict(value = "allDoctors", allEntries = true),
            @CacheEvict(value = "topDoctors", allEntries = true)
    })
    @Transactional
    @Override
    public DoctorResponse addDoctor(DoctorRequest request) {

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

    @Cacheable(value = "allDoctors")
    @Override
    public List<DoctorResponse> getAllDoctors() {
        List<DoctorEntity> doctors = doctorRepository.findAll();

        return doctors.stream().map(doctorMapper::fromDoctorEntity).toList();
    }

    @Cacheable(value = "doctorByIdCache", key = "#id")
    @Override
    public DoctorResponse getDoctorById(Long id) {

        DoctorEntity doctor = doctorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Doctor with id " + id + " not found")
        );

        return doctorMapper.fromDoctorEntity(doctor);
    }

    @Caching(evict = {
            @CacheEvict(value = "doctorByIdCache", key = "#id"),
            @CacheEvict(value = "allDoctors", allEntries = true),
            @CacheEvict(value = "topDoctors", allEntries = true)
    })
    @Override
    public DoctorResponse updateDoctor(Long id, DoctorRequest request) {
        DoctorEntity doctor = doctorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Doctor with id " + id + " not found")
        );

        doctor.setFullName(request.fullName());
        doctor.setEmail(request.email());
        doctor.setHospital(request.hospital());

        DoctorEntity doctor1 = doctorRepository.save(doctor);

        return doctorMapper.fromDoctorEntity(doctor1);
    }


    @Caching(
            evict = {
                    @CacheEvict(value = "doctorByIdCache", key = "#id"),
                    @CacheEvict(value = "allDoctors", allEntries = true)
            }
    )
    @Override
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    @Cacheable(value = "topDoctors")
    @Override
    public List<DoctorResponse> getTopDoctors() {
        List<DoctorEntity> topDoctors = doctorRepository.getTopDoctor();
        return topDoctors.stream().map(doctorMapper::fromDoctorEntity).toList();
    }
}
