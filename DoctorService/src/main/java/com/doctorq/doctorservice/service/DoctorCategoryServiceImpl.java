package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.DoctorCategoryRequest;
import com.doctorq.doctorservice.dtos.DoctorCategoryResponse;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import com.doctorq.doctorservice.exception.ConflictException;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.mapper.DoctorCategoryMapper;
import com.doctorq.doctorservice.repository.DoctorCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorCategoryServiceImpl implements DoctorCategoryService {

    private final DoctorCategoryRepository doctorCategoryRepository;
    private final DoctorCategoryMapper doctorCategoryMapper;

    @Override
    public DoctorCategoryEntity addCategory(DoctorCategoryRequest request) {

        if (doctorCategoryRepository.findByName(request.name()).isPresent())
            throw new ConflictException("Category already added");

        return doctorCategoryRepository.save(doctorCategoryMapper.toDoctorsCategoryEntity(request));
    }

    @Cacheable(value = "doctorsCategory")
    @Override
    public List<DoctorCategoryResponse> getAllCategories() {
        List<DoctorCategoryEntity> allCategories = doctorCategoryRepository.findAll();

        return allCategories.stream().map(doctorCategoryMapper::fromDoctorEntity)
                .toList();
    }

    //    @Cacheable(value = "DoctorCategoryEntity", key = "#id")
//    @Transactional(readOnly = true)
    @Override
    public DoctorCategoryEntity getCategoryById(Long id) {
        return doctorCategoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category not found")
        );
    }

    @CacheEvict(value = "#DoctorCategoryEntity", key = "id")
    @Override
    public DoctorCategoryEntity updateCategory(Long id, DoctorCategoryRequest request) {
        DoctorCategoryEntity doctorCategoryEntity = doctorCategoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category with id " + id + " not found")
        );

        doctorCategoryEntity.setName(request.name());
        doctorCategoryEntity.setDescription(request.description());

        return doctorCategoryRepository.save(doctorCategoryEntity);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "DoctorCategoryEntity", key = "#id"),
                    @CacheEvict(value = "doctorsCategory", allEntries = true)
            }
    )
    @Override
    public void deleteCategory(Long id) {
        doctorCategoryRepository.deleteById(id);
    }
}
