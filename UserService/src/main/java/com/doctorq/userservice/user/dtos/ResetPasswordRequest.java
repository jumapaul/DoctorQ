package com.doctorq.userservice.user.dtos;

public record ResetPasswordRequest(
        String email,
        String resetCode,
        String newPassword
) {
}
