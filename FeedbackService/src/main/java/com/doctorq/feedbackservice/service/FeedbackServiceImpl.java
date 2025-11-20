package com.doctorq.feedbackservice.service;

import com.doctorq.feedbackservice.doctor_client.DoctorClient;
import com.doctorq.feedbackservice.doctor_client.DoctorResponse;
import com.doctorq.feedbackservice.dtos.FeedbackRequest;
import com.doctorq.feedbackservice.entity.FeedbackEntity;
import com.doctorq.feedbackservice.exception.ResourceNotFoundException;
import com.doctorq.feedbackservice.exception.ServiceUnavailableException;
import com.doctorq.feedbackservice.mapper.FeedbackMapper;
import com.doctorq.feedbackservice.outer_box.FeedOuterBoxRepository;
import com.doctorq.feedbackservice.outer_box.FeedbackOuterBoxEntity;
import com.doctorq.feedbackservice.repository.FeedbackRepository;
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
    private final DoctorClient doctorClient;
    private final FeedOuterBoxRepository outerBoxRepository;

    @Transactional
    @Override
    public FeedbackEntity addFeedback(FeedbackRequest request, String token) {
        getDoctorById(request);
        DoctorResponse doctorResponse = doctorClient.getDoctorById(request.doctorId()).getData();

        FeedbackEntity savedFeed = feedbackRepository.save(feedbackMapper.toFeedback(request));

        double previousAverageRating = doctorResponse.rating();
        int previousRatingCount = doctorResponse.ratingsCount();
        double newAverage = 0;

        if (request.rating() != null) {
            if (previousRatingCount == 0) {
                newAverage = request.rating();
            } else {
                newAverage = ((previousAverageRating * previousRatingCount) + request.rating())
                        / (previousRatingCount + 1);
            }
        }

        FeedbackOuterBoxEntity entity;

        if (request.rating() == null) {
            entity = feedbackMapper.toFeedbackOuterBox(savedFeed, previousAverageRating,
                    previousRatingCount, doctorResponse.reviewsCount()+1);
        } else if (request.review() == null || request.review().isEmpty()) {
            entity = feedbackMapper.toFeedbackOuterBox(savedFeed, newAverage,
                    previousRatingCount + 1, doctorResponse.reviewsCount());
        } else {
            entity = feedbackMapper.toFeedbackOuterBox(savedFeed, newAverage,
                    previousRatingCount + 1, doctorResponse.reviewsCount()+1);
        }

        outerBoxRepository.save(entity);
        return savedFeed;
    }

    @CircuitBreaker(name = "doctorCircuitBreaker", fallbackMethod = "doctorFallback")
    private void getDoctorById(FeedbackRequest request) {
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
        entity.setReview(request.review());

        return feedbackRepository.save(entity);
    }

    private FeedbackEntity doctorFallback(FeedbackRequest request, String token, Exception ex) {
        throw new ServiceUnavailableException(
                "Doctor service is temporarily unavailable. Please try again later"
        );
    }

    private FeedbackEntity userFallback(FeedbackRequest request, String token, Exception ex) {
        throw new ServiceUnavailableException(
                "User service is temporarily unavailable. Please try again later"
        );
    }
}
