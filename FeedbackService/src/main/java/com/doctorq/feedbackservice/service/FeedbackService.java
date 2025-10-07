package com.doctorq.feedbackservice.service;

import com.doctorq.feedbackservice.dtos.FeedbackRequest;
import com.doctorq.feedbackservice.entity.FeedbackEntity;
import com.doctorq.feedbackservice.response.ApiResponse;

import java.util.List;

public interface FeedbackService {

    ApiResponse<FeedbackEntity> addFeedback(FeedbackRequest request, String authHeader);

    ApiResponse<String> deleteFeedback(Long id);

    ApiResponse<List<FeedbackEntity>> getAllDoctorFeedback(Long doctorId);

    ApiResponse<FeedbackEntity> updateFeedback(Long id, FeedbackRequest request);
}
