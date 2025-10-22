package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.DoctorCategoryRequest;
import com.doctorq.doctorservice.dtos.DoctorCategoryResponse;
import com.doctorq.doctorservice.dtos.DoctorDto;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import com.doctorq.doctorservice.exception.ConflictException;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.mapper.DoctorCategoryMapper;
import com.doctorq.doctorservice.repository.DoctorCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorCategoryServiceImpl implements DoctorCategoryService {

    private final DoctorCategoryRepository doctorCategoryRepository;
    private final DoctorCategoryMapper doctorCategoryMapper;

    @CacheEvict(value = "allDoctorsCategory", allEntries = true)
    @Override
    public DoctorCategoryEntity addCategory(DoctorCategoryRequest request) {

        if (doctorCategoryRepository.findByName(request.name()).isPresent())
            throw new ConflictException("Category already added");

        return doctorCategoryRepository.save(doctorCategoryMapper.toDoctorsCategoryEntity(request));
    }

    @Cacheable(value = "allDoctorsCategory")
    @Override
    public List<DoctorCategoryEntity> getAllCategories() {
        return doctorCategoryRepository.findAll();

//        return allCategories.stream().map(doctorCategoryMapper::fromDoctorEntity)
//                .toList();
    }

    @Cacheable(value = "doctorCategoryByIdCache", key = "#id")
    @Override
    public DoctorCategoryResponse getCategoryById(Long id) {
        DoctorCategoryEntity doctorCategory = doctorCategoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category not found")
        );

        List<DoctorDto> doctors = doctorCategory.getDoctors().stream()
                .map(doctorCategoryMapper::fromEntity).toList();

        return new DoctorCategoryResponse(
                doctorCategory.getId(),
                doctorCategory.getCategoryIcon(),
                doctorCategory.getDescription(),
                doctorCategory.getName(),
                doctorCategory.getDoctorsCount(),
                doctors
        );
    }

    @Caching(
            evict = {@CacheEvict(value = "allDoctorsCategory", allEntries = true)},
            put = {@CachePut(value = "doctorCategoryByIdCache", key = "id"),}
    )
    @Override
    public DoctorCategoryResponse updateCategory(Long id, DoctorCategoryRequest request) {
        DoctorCategoryEntity doctorCategoryEntity = doctorCategoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category with id " + id + " not found")
        );

        doctorCategoryEntity.setName(request.name());
        doctorCategoryEntity.setDescription(request.description());

        DoctorCategoryEntity doctorCategory = doctorCategoryRepository.save(doctorCategoryEntity);

        List<DoctorDto> doctors = doctorCategory.getDoctors().stream()
                .map(doctorCategoryMapper::fromEntity).toList();
        return new DoctorCategoryResponse(
                doctorCategory.getId(),
                doctorCategory.getCategoryIcon(),
                doctorCategory.getDescription(),
                doctorCategory.getName(),
                doctorCategory.getDoctorsCount(),
                doctors
        );
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "doctorCategoryByIdCache", key = "#id"),
                    @CacheEvict(value = "doctorsCategory", allEntries = true)
            }
    )
    @Override
    public void deleteCategory(Long id) {
        doctorCategoryRepository.deleteById(id);
    }
}
