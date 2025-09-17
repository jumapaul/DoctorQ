package com.doctorq.feedbackservice.service;

import com.doctorq.feedbackservice.doctor_client.DoctorClient;
import com.doctorq.feedbackservice.dtos.FeedbackRequest;
import com.doctorq.feedbackservice.entity.FeedbackEntity;
import com.doctorq.feedbackservice.exception.ResourceNotFoundException;
import com.doctorq.feedbackservice.mapper.FeedbackMapper;
import com.doctorq.feedbackservice.repository.FeedbackRepository;
import com.doctorq.feedbackservice.response.ApiResponse;
import com.doctorq.feedbackservice.user_client.UserClient;
import com.doctorq.feedbackservice.user_client.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;
    private final UserClient userClient;
    private final DoctorClient doctorClient;

    @Transactional
    @Override
    public ApiResponse<FeedbackEntity> addFeedback(FeedbackRequest request, String authHeader) {

        ApiResponse<UserResponseDto> user = userClient.getUserById(request.userId(), authHeader);

        doctorClient.getDoctorById(request.doctorId(), authHeader);

        FeedbackEntity feedbackEntity = feedbackRepository.save(feedbackMapper.toFeedback(request, user.getData().firstname()));

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Feedback added successfully",
                feedbackEntity
        );
    }

    @Override
    public ApiResponse<String> deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Feedback successfully deleted",
                null
        );
    }

    @Override
    public ApiResponse<List<FeedbackEntity>> getAllDoctorFeedback(Long doctorId) {
        List<FeedbackEntity> doctorFeeds = feedbackRepository.findByDoctorId(doctorId);

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Doctor feeds retrieved",
                doctorFeeds
        );
    }

    @Override
    public ApiResponse<FeedbackEntity> updateFeedback(Long id, FeedbackRequest request) {
        FeedbackEntity entity = feedbackRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Feedback of id " + id + " not found")
        );

        entity.setId(id);
        entity.setUserId(request.userId());
        entity.setDoctorId(request.doctorId());
        entity.setRating(request.rating());
        entity.setReview(request.review());

        feedbackRepository.save(entity);
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Feedback updated successfully",
                entity
        );
    }
}
