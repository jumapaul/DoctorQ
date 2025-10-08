package com.doctorq.userservice.user.dtos;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {
}
