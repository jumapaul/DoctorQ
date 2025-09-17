package com.doctorq.feedbackservice.user_client;

public record UserResponseDto(
        Long id,
        String firstname,
        String lastname,
        String email
) {
}
