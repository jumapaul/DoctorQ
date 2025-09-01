package com.doctorq.userservice.user_profile.dtos;
import com.doctorq.userservice.user.entities.User;

public record UserProfileRequest(
        String gender,
        String dateOfBirth,
        String address,
        String profileUrl
) {
}
