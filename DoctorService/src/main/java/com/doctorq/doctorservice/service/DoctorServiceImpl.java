package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.Roles;
import com.doctorq.doctorservice.entities.WorkingHours;
import com.doctorq.doctorservice.feign_client.UserClient;
import com.doctorq.doctorservice.repository.WorkingHoursRepository;
import com.doctorq.doctorservice.dtos.response.DoctorOverview;
import com.doctorq.doctorservice.dtos.response.PaginatedResponse;
import com.doctorq.doctorservice.utils.RedisUtil;
import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.dtos.response.DoctorResponse;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.doctorq.doctorservice.utils.Constants.*;
import static com.doctorq.doctorservice.utils.RedisReadWriteMethods.*;

/**
 * DoctorService
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final RedisUtil redisUtil;
    private final DoctorRepository doctorRepository;
    private final DoctorCategoryRepository doctorCategoryRepository;
    private final WorkingHoursRepository workingHoursRepository;
    private final DoctorMapper doctorMapper;
    private final UserClient userClient;

    @Transactional
    @Override
    public DoctorResponse addDoctor(DoctorRequest request) {
        redisUtil.deleteGroup(allDoctorsCache);
        Optional<DoctorEntity> existingDoctor = doctorRepository.findByEmail(request.email());

        if (existingDoctor.isPresent()) throw new ConflictException("Doctor already exists");


        Set<DoctorCategoryEntity> categoryEntities = request.doctorCategory().stream().map(categoryId ->
                doctorCategoryRepository.findById(categoryId).orElseThrow(() ->
                        new ResourceNotFoundException("Category with id not found")
                )
        ).collect(Collectors.toSet());

        WorkingHours workingHours = workingHoursRepository.save(doctorMapper.toWorkingHours(request));
        workingHoursRepository.save(workingHours);
        DoctorEntity doctor = doctorMapper.toDoctorEntity(request, workingHours);
        doctor.setDoctorCategory(categoryEntities);
        doctor = doctorRepository.save(doctor);

        categoryEntities.forEach(category ->
                doctorCategoryRepository.incrementDoctorCount(category.getId())
        );

        return doctorMapper.fromDoctorEntity(doctor);
    }

    @Override
    public PaginatedResponse<DoctorOverview> getAllDoctors(int page, int size, boolean sortByRating) throws JsonProcessingException {
        String cacheKey = allDoctorsCache + "::" + page + size;
        Object doctorsCache = redisUtil.get(cacheKey);

        if (doctorsCache != null) return readCacheValue(doctorsCache, new TypeReference<>() {
        });

        Sort sort = sortByRating ? Sort.by(Sort.Direction.DESC, "rating") : Sort.unsorted();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DoctorEntity> doctors = doctorRepository.findAll(pageable);
        List<DoctorOverview> response = doctors.stream()
                .map(doctorMapper::fromDoctorEntityToOverview)
                .toList();
        PaginatedResponse<DoctorOverview> paginatedResponse = paginate(response, doctors);

        setGroupCacheValue(redisUtil, cacheKey, paginatedResponse);

        return paginatedResponse;
    }

    @Override
    public DoctorResponse getDoctorById(Long id) throws JsonProcessingException {
        String cacheKey = doctorByIdCache + id;
        Object doctorCache = redisUtil.get(cacheKey);

        if (doctorCache != null) return readCacheValue(doctorCache, new TypeReference<>() {
        });

        DoctorEntity doctor = doctorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Doctor with id " + id + " not found")
        );

        DoctorResponse response = doctorMapper.fromDoctorEntity(doctor);

        setCacheValue(redisUtil, cacheKey, response);
        return response;
    }

    @Override
    @Transactional
    public DoctorResponse updateDoctor(Long id, DoctorRequest request) throws JsonProcessingException {
        redisUtil.deleteGroup(allDoctorsCache);
        redisUtil.deleteGroup(topDoctorsCache);
        redisUtil.delete(doctorByIdCache + id);
        redisUtil.deleteGroup(userFavoriteDoctors);
        DoctorEntity doctor = doctorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Doctor with id " + id + " not found")
        );

        return updateDoctor(request, doctor);
    }

    private DoctorResponse updateDoctor(DoctorRequest request, DoctorEntity doctor) {
        doctor.setFullName(request.fullName());
        doctor.setEmail(request.email());
        doctor.setHospital(request.hospital());
        doctor.setRating(doctor.getRating());
        doctor.setReviewsCount(doctor.getRatingCount());
        doctor.setRole(Roles.DOCTOR);
        doctor.setDoctorCategory(doctor.getDoctorCategory());
        doctor.setAboutDoctor(request.aboutDoctor());
        doctor.setYearsOfExperience(request.yearsOfExperience());

        DoctorEntity savedDoctor = doctorRepository.save(doctor);
        return doctorMapper.fromDoctorEntity(savedDoctor);
    }

    @Transactional
    @Override
    public void deleteDoctor(Long id, String token) {
        redisUtil.delete(doctorByIdCache + id);
        redisUtil.deleteGroup(allDoctorsCache);
        redisUtil.deleteGroup(topDoctorsCache);
        redisUtil.deleteGroup(userFavoriteDoctors);
        redisUtil.delete(categoryTopDoctorCache + "*");
        DoctorEntity doctor = doctorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Doctor not found")
        );
        userClient.assignUserRole(doctor.getUserId(), token);
        doctorRepository.delete(doctor);
    }

    @Override
    public PaginatedResponse<DoctorOverview> getTopDoctors(int page, int size) throws JsonProcessingException {
        String cacheKey = topDoctorsCache + page + size;
        Object topDoctorsCache = redisUtil.get(cacheKey);

        if (topDoctorsCache != null) return readCacheValue(topDoctorsCache, new TypeReference<>() {
        });

        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorEntity> topDoctors = doctorRepository.getTopDoctor(pageable);
        List<DoctorOverview> response = topDoctors.stream().map(doctorMapper::fromDoctorEntityToOverview).toList();

        PaginatedResponse<DoctorOverview> paginatedResponse = paginate(response, topDoctors);
        setGroupCacheValue(redisUtil, cacheKey, paginatedResponse);
        return paginatedResponse;
    }

    @Override
    public PaginatedResponse<DoctorOverview> getCategoryTopDoctor(int page, int size, Long categoryId) throws JsonProcessingException {

        log.info("----------->Category top doctor method is called");
        String cacheKey = categoryTopDoctorCache + page + size + categoryId;

        Object categoriesTopDoctorCache = redisUtil.get(cacheKey);

        log.info("--------> {}", categoriesTopDoctorCache);

        if (categoriesTopDoctorCache != null) return readCacheValue(categoriesTopDoctorCache, new TypeReference<>() {
        });

        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorEntity> topDoctors = doctorRepository.getCategoryTopDoctor(categoryId, pageable);
        List<DoctorOverview> response = topDoctors.stream().map(doctorMapper::fromDoctorEntityToOverview).toList();

        PaginatedResponse<DoctorOverview> paginatedResponse = paginate(response, topDoctors);
        log.info("------------>Paginated response is: {}", paginatedResponse);

        setGroupCacheValue(redisUtil, cacheKey, paginatedResponse);
        return paginatedResponse;
    }

    @Override
    public PaginatedResponse<DoctorOverview> searchDoctorByName(String name, int page, int size) throws JsonProcessingException {
        String cacheKey = searchedDoctorsCache + name + page + size;
        PaginatedResponse<DoctorOverview> searchedDoctor = readCacheValue(cacheKey, new TypeReference<>() {
        });

        if (searchedDoctor != null) return searchedDoctor;

        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorEntity> searchedDoctors = doctorRepository.findAllByFullName(name, pageable);
        List<DoctorOverview> response = searchedDoctors.stream().map(doctorMapper::fromDoctorEntityToOverview).toList();

        PaginatedResponse<DoctorOverview> paginatedResponse = paginate(response, searchedDoctors);
        redisUtil.setGroup(cacheKey, paginatedResponse.data());
//            setGroupCacheValue(redisUtil, searchedDoctorsCache + name + page + size, paginatedResponse.data());
        return paginatedResponse;
    }

    @Override
    public DoctorResponse updateSchedule(WorkingHours workingHours) {
        WorkingHours workTime = workingHoursRepository.findById(workingHours.getId()).orElseThrow(() ->
                new ResourceNotFoundException("")
        );

        workTime.setDate(workingHours.getDate());
        workTime.setStartTime(workingHours.getStartTime());
        workTime.setEndTime(workTime.getEndTime());
        workingHoursRepository.save(workingHours);

        return doctorMapper.fromDoctorEntity(workTime.getDoctor());
    }

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
