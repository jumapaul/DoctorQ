package com.doctorq.userservice.user.dtos;

import com.doctorq.userservice.user.Roles;

public record LoginResponse(
        Long id,
        String username,
        String email,
        String token,
        Roles role
) {
}
