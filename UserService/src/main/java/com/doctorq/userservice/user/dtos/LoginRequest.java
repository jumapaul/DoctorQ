package com.doctorq.userservice.user.dtos;

public record LoginRequest(
        String email,
        String password
) {
}
