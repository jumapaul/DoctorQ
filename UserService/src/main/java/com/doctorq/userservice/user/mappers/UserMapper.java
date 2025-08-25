package com.doctorq.userservice.user.mappers;

import com.doctorq.userservice.user.Roles;
import com.doctorq.userservice.user.dtos.LoginResponse;
import com.doctorq.userservice.user.dtos.RegisterResponse;
import com.doctorq.userservice.user.dtos.RegisterUserDto;
import com.doctorq.userservice.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;

    public User toUser(RegisterUserDto registerUserDto, String code) {
        return User.builder()
                .username(registerUserDto.username())
                .email(registerUserDto.email())
                .password(passwordEncoder.encode(registerUserDto.password()))
                .isEnabled(false)
                .verificationCode(code)
                .verificationExpiresAt(LocalDateTime.now().plusMinutes(30))
                .role(Roles.USER)
                .build();
    }

    public RegisterResponse fromUser(User user) {
        return new RegisterResponse(
                user.getUsername(),
                user.getEmail(),
                user.getVerificationCode(),
                user.getRole().name()
        );
    }

    public LoginResponse fromLoggedInUser(User user, String token) {
        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                token,
                user.getRole()
        );
    }
}
