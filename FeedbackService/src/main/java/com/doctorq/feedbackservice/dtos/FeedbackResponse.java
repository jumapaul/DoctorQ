package com.doctorq.feedbackservice.dtos;

public record FeedbackResponse(
        Long id,
        String userId,
        String username,
        Long doctorId,
        Long doctorName
) {
}
