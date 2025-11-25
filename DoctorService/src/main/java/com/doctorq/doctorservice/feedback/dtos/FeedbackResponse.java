package com.doctorq.doctorservice.feedback.dtos;

public record FeedbackResponse(
        Long id,
        String userId,
        String username,
        Long doctorId,
        Long doctorName
) {
}
