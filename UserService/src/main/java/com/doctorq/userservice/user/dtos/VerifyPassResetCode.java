package com.doctorq.userservice.user.dtos;

public record VerifyPassResetCode(
        String email,
        String resetPassCode
) {
}
