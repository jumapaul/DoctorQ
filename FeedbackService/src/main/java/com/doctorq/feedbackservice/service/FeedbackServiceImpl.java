package com.doctorq.feedbackservice.service;

import com.doctorq.feedbackservice.doctor_client.DoctorClient;
import com.doctorq.feedbackservice.dtos.FeedbackRequest;
import com.doctorq.feedbackservice.entity.FeedbackEntity;
import com.doctorq.feedbackservice.exception.ResourceNotFoundException;
import com.doctorq.feedbackservice.exception.ServiceUnavailableException;
import com.doctorq.feedbackservice.mapper.FeedbackMapper;
import com.doctorq.feedbackservice.repository.FeedbackRepository;
import com.doctorq.feedbackservice.response.ApiResponse;
import com.doctorq.feedbackservice.user_client.UserClient;
import com.doctorq.feedbackservice.user_client.UserResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public FeedbackEntity addFeedback(FeedbackRequest request, String token) {

        ApiResponse<UserResponseDto> user = getUserById(request, token);
        getDoctorById(request);
        doctorClient.getDoctorById(request.doctorId());

        return feedbackRepository.save(feedbackMapper.toFeedback(request, user.getData().firstname()));
    }

    @CircuitBreaker(name = "userCircuitBreaker", fallbackMethod = "userFallback")
    private ApiResponse<UserResponseDto> getUserById(FeedbackRequest request, String token) {
        return userClient.getUserById(request.userId(), token);
    }

    @CircuitBreaker(name = "doctorCircuitBreaker", fallbackMethod = "doctorFallback")
    private void  getDoctorById(FeedbackRequest request) {
        doctorClient.getDoctorById(request.doctorId());
    }

    @Override
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }

    @Override
    public List<FeedbackEntity> getAllDoctorFeedback(Long doctorId) {
        return feedbackRepository.findByDoctorId(doctorId);

    }

    @Override
    public FeedbackEntity updateFeedback(Long id, FeedbackRequest request) {
        FeedbackEntity entity = feedbackRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Feedback of id " + id + " not found")
        );

        entity.setId(id);
        entity.setUserId(request.userId());
        entity.setDoctorId(request.doctorId());
        entity.setRating(request.rating());
        entity.setReview(request.review());

        return feedbackRepository.save(entity);
    }

    private FeedbackEntity doctorFallback(FeedbackRequest request, String token, Exception ex) {
        log.error("Circuit breaker activated: {}", ex.getMessage());
        throw new ServiceUnavailableException(
                "Doctor service is temporarily unavailable. Please try again later"
        );
    }

    private FeedbackEntity userFallback(FeedbackRequest request, String token, Exception ex) {
        log.error("Circuit breaker activated: {}", ex.getMessage());
        throw new ServiceUnavailableException(
                "User service is temporarily unavailable. Please try again later"
        );
    }
}
