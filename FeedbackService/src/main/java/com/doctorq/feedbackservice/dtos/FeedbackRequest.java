package com.doctorq.feedbackservice.dtos;

import jakarta.validation.constraints.Max;

public record FeedbackRequest(
        Long userId,
        Long doctorId,
        @Max(value = 10, message = "The value should not exceed 10")
        Integer rating,
        String review
) {
}
