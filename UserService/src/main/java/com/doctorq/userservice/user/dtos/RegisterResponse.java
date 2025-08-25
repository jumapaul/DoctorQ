package com.doctorq.userservice.user.dtos;

public record RegisterResponse(
        String username,
        String email,
        String verificationCode,
        String role
) {
}
