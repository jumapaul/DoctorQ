package com.doctorq.userservice.user_profile.dtos;

import com.doctorq.userservice.user.Roles;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        Roles roles,
        UserProfile userProfile
) {
}
