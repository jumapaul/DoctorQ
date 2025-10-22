package com.doctorq.feedbackservice.service;

import com.doctorq.feedbackservice.dtos.FeedbackRequest;
import com.doctorq.feedbackservice.entity.FeedbackEntity;
import com.doctorq.feedbackservice.response.ApiResponse;

import java.util.List;

public interface FeedbackService {

    FeedbackEntity addFeedback(FeedbackRequest request, String token);

    void deleteFeedback(Long id);

    List<FeedbackEntity> getAllDoctorFeedback(Long doctorId);

    FeedbackEntity updateFeedback(Long id, FeedbackRequest request);
}
