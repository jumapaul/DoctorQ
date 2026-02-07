package com.doctorq.doctorservice.feedback.service;

import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.exception.ServiceUnavailableException;
import com.doctorq.doctorservice.feedback.dtos.FeedbackRequest;
import com.doctorq.doctorservice.feedback.entity.FeedbackEntity;
import com.doctorq.doctorservice.feedback.mapper.FeedbackMapper;
import com.doctorq.doctorservice.feedback.repository.FeedbackRepository;
import com.doctorq.doctorservice.repository.DoctorRepository;
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
    private final DoctorRepository doctorRepository;

    @Transactional
    @Override
    public FeedbackEntity addFeedback(FeedbackRequest request, String token) {
        DoctorEntity doctorResponse = doctorRepository.findById(request.doctorId()).orElseThrow(() ->
                new ResourceNotFoundException("Doctor with id " + request.doctorId() + " not found")
        );
        FeedbackEntity savedFeed = feedbackRepository.save(feedbackMapper.toFeedback(request, doctorResponse));

        double previousAverageRating = doctorResponse.getRating();
        int previousRatingCount = doctorResponse.getRatingCount();

        double newAverage;

        //If rating is provided, calculate the average as the new rating
        if (request.rating() != null) {
            //If previous rating count is 0, then the new rating is the average
            if (previousRatingCount == 0) {
                newAverage = request.rating();
            } else {
                newAverage = ((previousAverageRating * previousRatingCount) + request.rating())
                        / (previousRatingCount + 1);
            }

            doctorResponse.setRatingCount(doctorResponse.getRatingCount() + 1);
            doctorResponse.setRating(newAverage);
        }

        if (request.review() != null) {
            doctorResponse.setReviewsCount(doctorResponse.getReviewsCount() + 1);
        }

        return savedFeed;
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
