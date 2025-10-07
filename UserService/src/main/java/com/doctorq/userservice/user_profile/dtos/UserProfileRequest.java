package com.doctorq.userservice.user_profile.dtos;

public record UserProfileRequest(
        String gender,
        String dateOfBirth,
        String address,
        String profileUrl
) {
}
