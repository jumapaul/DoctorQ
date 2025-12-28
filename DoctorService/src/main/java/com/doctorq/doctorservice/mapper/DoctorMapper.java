package com.doctorq.doctorservice.mapper;

import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.dtos.response.WorkingHoursResponse;
import com.doctorq.doctorservice.entities.WorkingHours;
import com.doctorq.doctorservice.dtos.response.DoctorOverview;
import com.doctorq.doctorservice.dtos.response.DoctorResponse;
import com.doctorq.doctorservice.dtos.Roles;
import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.repository.DoctorCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorMapper {
    private final DoctorCategoryRepository doctorCategoryRepository;

    public DoctorEntity toDoctorEntity(DoctorRequest request, WorkingHours workingHours) {

        Set<DoctorCategoryEntity> categoryEntitySet = request.doctorCategory()
                .stream()
                .map(id -> doctorCategoryRepository.findById(id).orElseThrow(() ->
                        new ResourceNotFoundException("Category of the id " + id + " not found")
                )).collect(Collectors.toSet());

        return DoctorEntity.builder()
                .fullName(request.fullName())
                .email(request.email())
                .profilePictureUrl(request.profilePicUrl())
                .hospital(request.hospital())
                .ratingCount(0)
                .reviewsCount(0)
                .role(Roles.DOCTOR)
                .doctorCategory(categoryEntitySet)
                .rating(0.0)
                .aboutDoctor(request.aboutDoctor())
                .numberOfPatients(0)
                .yearsOfExperience(request.yearsOfExperience())
                .schedule(workingHours)
                .build();
    }

    public DoctorResponse fromDoctorEntity(DoctorEntity entity) {
        List<String> categories = entity.getDoctorCategory().stream().map(DoctorCategoryEntity::getName).toList();
        WorkingHoursResponse workingHoursResponse = fromWorkingHours(entity.getSchedule());
        return new DoctorResponse(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getProfilePictureUrl(),
                entity.getHospital(),
                categories,
                entity.getRating(),
                entity.getRole(),
                entity.getRatingCount(),
                entity.getReviewsCount(),
                entity.getAboutDoctor(),
                entity.getYearsOfExperience(),
                entity.getNumberOfPatients(),
                workingHoursResponse
        );
    }

    public WorkingHoursResponse fromWorkingHours(WorkingHours workingHours) {
        return new WorkingHoursResponse(
                workingHours.getId(),
                workingHours.getDate().toString(),
                workingHours.getStartTime().toString(),
                workingHours.getEndTime().toString()
        );
    }

    public DoctorOverview fromDoctorEntityToOverview(DoctorEntity entity) {
        List<String> categories = entity.getDoctorCategory().stream().map(DoctorCategoryEntity::getName).toList();
        return new DoctorOverview(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getProfilePictureUrl(),
                entity.getHospital(),
                categories,
                entity.getRating(),
                entity.getReviewsCount()
        );
    }

    public WorkingHours toWorkingHours(DoctorRequest request) {
        return WorkingHours.builder()
                .date(request.schedule().date())
                .startTime(request.schedule().startTime())
                .endTime(request.schedule().endTime())
                .build();
    }
}
