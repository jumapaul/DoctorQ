package com.doctorq.userservice.user.dtos;

import com.doctorq.userservice.user.Roles;
import com.doctorq.userservice.user_profile.dtos.UserProfile;

public record LoginResponse(
        Long id,
        String firstname,
        String lastname,
        String email,
        String token,
        Roles role,
        UserProfile userProfile
) {
}
