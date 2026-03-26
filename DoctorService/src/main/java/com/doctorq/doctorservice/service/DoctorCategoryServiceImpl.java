package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.DoctorCategoryRequest;
import com.doctorq.doctorservice.dtos.response.DoctorCategoryResponse;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import com.doctorq.doctorservice.exception.ConflictException;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.mapper.DoctorCategoryMapper;
import com.doctorq.doctorservice.repository.DoctorCategoryRepository;
import com.doctorq.doctorservice.dtos.response.DoctorOverview;
import com.doctorq.doctorservice.utils.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.doctorq.doctorservice.utils.Constants.*;
import static com.doctorq.doctorservice.utils.RedisReadWriteMethods.readCacheValue;
import static com.doctorq.doctorservice.utils.RedisReadWriteMethods.setCacheValue;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorCategoryServiceImpl implements DoctorCategoryService {

    private final DoctorCategoryRepository doctorCategoryRepository;
    private final DoctorCategoryMapper doctorCategoryMapper;
    private final RedisUtil redisUtil;

    @Transactional
    @Override
    public DoctorCategoryEntity addCategory(DoctorCategoryRequest request) {

        redisUtil.delete(allDoctorsCategoryCache);
        if (doctorCategoryRepository.findByName(request.name()).isPresent())
            throw new ConflictException("Category already added");

        return doctorCategoryRepository.save(doctorCategoryMapper.toDoctorsCategoryEntity(request));
    }

    @Override
    public List<DoctorCategoryEntity> getAllCategories() throws JsonProcessingException {
        Object cachedAllCategories = redisUtil.get(allDoctorsCategoryCache);

        if (cachedAllCategories != null) return readCacheValue(cachedAllCategories, new TypeReference<>() {
        });

        List<DoctorCategoryEntity> response = doctorCategoryRepository.findAll();
        setCacheValue(redisUtil, allDoctorsCategoryCache, response);
        return response;
    }

    @Override
    public DoctorCategoryResponse getCategoryById(Long id) throws JsonProcessingException {

        String cacheKey = doctorCategoryByIdCache + id;
        Object cachedCategoryById = redisUtil.get(cacheKey);

        if (cachedCategoryById != null) return readCacheValue(cachedCategoryById, new TypeReference<>() {
        });

        DoctorCategoryEntity doctorCategory = doctorCategoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category not found")
        );

        setCacheValue(redisUtil, cacheKey, doctorCategory);
        return getDoctorCategoryResponse(id, doctorCategory);
    }

    @Override
    public DoctorCategoryResponse updateCategory(Long id, DoctorCategoryRequest request) throws JsonProcessingException {
        String cacheKey = doctorCategoryByIdCache + id;
        redisUtil.delete(allDoctorsCategoryCache);
        redisUtil.delete(cacheKey);
        DoctorCategoryEntity doctorCategoryEntity = doctorCategoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category with id " + id + " not found")
        );

        doctorCategoryEntity.setName(request.name());
        doctorCategoryEntity.setDescription(request.description());

        DoctorCategoryEntity doctorCategory = doctorCategoryRepository.save(doctorCategoryEntity);

        return getDoctorCategoryResponse(id, doctorCategory);
    }

    private DoctorCategoryResponse getDoctorCategoryResponse(Long id, DoctorCategoryEntity doctorCategory) throws JsonProcessingException {
        String cacheKey = doctorCategoryByIdCache + id;
        DoctorCategoryResponse doctorCategoryResponse = readCacheValue(cacheKey, new TypeReference<>() {
        });

        if (doctorCategoryResponse != null) return doctorCategoryResponse;
        List<DoctorOverview> doctors = doctorCategory.getDoctors().stream()
                .map(doctorCategoryMapper::fromEntity).toList();
        DoctorCategoryResponse response = new DoctorCategoryResponse(
                doctorCategory.getId(),
                doctorCategory.getCategoryIcon(),
                doctorCategory.getDescription(),
                doctorCategory.getName(),
                doctorCategory.getDoctorsCount(),
                doctors
        );
        setCacheValue(redisUtil, cacheKey, response);
        return response;
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        redisUtil.delete(doctorCategoryByIdCache);
        redisUtil.delete(allDoctorsCategoryCache);
        doctorCategoryRepository.deleteById(id);
    }
}
