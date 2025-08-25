package com.doctorq.userservice.user.dtos;

import jakarta.validation.constraints.Email;

public record RegisterUserDto(
        String username,
        @Email(message = "Invalid email format")
        String email,
        String password
) {
}
