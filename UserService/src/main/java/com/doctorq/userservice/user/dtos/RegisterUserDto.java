package com.doctorq.userservice.user.dtos;

import jakarta.validation.constraints.*;

public record RegisterUserDto(
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstname,
        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastname,
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "password is required")
        @Size(message = "Password must be at least 8 characters")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
        )
        String password

//        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
//        message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit")
) {
}
