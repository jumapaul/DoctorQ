package com.doctorq.userservice.user.dtos;

public record VerifyUserDto(
        String email,
        String verificationCode
) {
}
