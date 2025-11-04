package com.doctorq.userservice.user.mappers;

import com.doctorq.userservice.user.Roles;
import com.doctorq.userservice.user.dtos.LoginResponse;
import com.doctorq.userservice.user.dtos.RegisterResponse;
import com.doctorq.userservice.user.dtos.RegisterUserDto;
import com.doctorq.userservice.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AuthMapper {
    private final PasswordEncoder passwordEncoder;

    public User toUser(RegisterUserDto registerUserDto, String code) {
        return User.builder()
                .firstname(registerUserDto.firstname())
                .lastname(registerUserDto.lastname())
                .email(registerUserDto.email())
                .password(passwordEncoder.encode(registerUserDto.password()))
                .isEnabled(false)
                .verificationCode(code)
                .verificationExpiresAt(OffsetDateTime.now().plusMinutes(30))
                .role(Roles.USER)
                .build();
    }

    public RegisterResponse fromUser(User user) {
        return new RegisterResponse(
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getVerificationCode(),
                user.getRole().name()
        );
    }

    public LoginResponse fromLoggedInUser(User user, String token, String refreshToken) {
        return new LoginResponse(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                token,
                refreshToken,
                user.getRole(),
                user.getUserProfile()
        );
    }
}
