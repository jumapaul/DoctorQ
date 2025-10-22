package com.doctorq.userservice.user_profile.dtos;

public record UserProfileResponse(
        Long id,
        String gender,
        String dateOfBirth,
        String address,
        String profileUrl
) {
}
