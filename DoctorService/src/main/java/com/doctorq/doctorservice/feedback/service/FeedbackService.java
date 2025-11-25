package com.doctorq.doctorservice.feedback.service;

import com.doctorq.doctorservice.feedback.dtos.FeedbackRequest;
import com.doctorq.doctorservice.feedback.entity.FeedbackEntity;

import java.util.List;

public interface FeedbackService {

    FeedbackEntity addFeedback(FeedbackRequest request, String token);

    void deleteFeedback(Long id);

    List<FeedbackEntity> getAllDoctorFeedback(Long doctorId);

    FeedbackEntity updateFeedback(Long id, FeedbackRequest request);
}
