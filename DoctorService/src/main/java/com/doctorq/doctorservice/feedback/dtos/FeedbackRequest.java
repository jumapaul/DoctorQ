package com.doctorq.doctorservice.feedback.dtos;

public record FeedbackRequest(
        Long userId,
        Long doctorId,
        Integer rating,
        String review,
        String username
) {
}
