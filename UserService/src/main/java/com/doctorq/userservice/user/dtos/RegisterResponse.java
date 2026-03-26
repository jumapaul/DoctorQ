package com.doctorq.userservice.user.dtos;

public record RegisterResponse(
        String firstname,
        String lastname,
        String email,
        String role
) {
}
