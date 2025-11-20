package com.doctorq.feedbackservice.dtos;

public record FeedbackRequest(
        Long userId,
        Long doctorId,
        Integer rating,
        String review,
        String username
) {
}
