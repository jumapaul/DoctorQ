package com.doctorq.userservice.user.service;

import com.doctorq.userservice.config.JwtService;
import com.doctorq.userservice.exception.BadRequestException;
import com.doctorq.userservice.exception.ConflictException;
import com.doctorq.userservice.exception.ForbiddenException;
import com.doctorq.userservice.mail.EmailService;
import com.doctorq.userservice.response.ApiResponse;
import com.doctorq.userservice.user.dtos.*;
import com.doctorq.userservice.user.entities.User;
import com.doctorq.userservice.user.mappers.AuthMapper;
import com.doctorq.userservice.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthMapper authMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public ApiResponse<RegisterResponse> registerUser(@Valid RegisterUserDto registerUserDto) throws MessagingException {

        boolean userExists = userRepository.findByEmail(registerUserDto.email()).isPresent();

        if (userExists) throw new ConflictException("User already exists");

        User user = userRepository.save(authMapper.toUser(registerUserDto, generateVerificationCode()));

        //Send email.
        emailService.sendVerificationCode(user.getEmail(), user.getVerificationCode());
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                registerUserDto.email() + " registered successfully",
                authMapper.fromUser(user)
        );
    }

    @Override
    public ApiResponse<String> verifyUser(VerifyUserDto verifyUserDto) {
        User user = userRepository.findByEmail(verifyUserDto.email()).orElseThrow(() ->
                new UsernameNotFoundException("User with email " + verifyUserDto.email() + " not found")
        );

        if (user.getVerificationCode() == null) throw new ConflictException("User already verified");

        if (user.getVerificationExpiresAt().isBefore(LocalDateTime.now()))
            throw new BadRequestException("Verification code already expired.");

        if (verifyUserDto.verificationCode().equals(user.getVerificationCode())) {
            user.setVerificationCode(null);
            user.setEnabled(true);

            userRepository.save(user);

            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "User verified successfully",
                    null
            );
        } else {
            throw new BadRequestException("Invalid verification code");
        }
    }

    @Override
    public ApiResponse<String> resendVerificationCode(String email) throws MessagingException {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(email + " not found")
        );

        if (user.isEnabled()) throw new BadRequestException("User already verified");
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpiresAt(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        emailService.sendVerificationCode(email, user.getVerificationCode());
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Verification code sent to email",
                null
        );
    }

    @Override
    public ApiResponse<LoginResponse> loginUser(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            User user = userRepository.findByEmail(request.email()).orElseThrow(() ->
                    new UsernameNotFoundException("User with the email " + request.email() + " not found")
            );

            if (!user.isEnabled()) throw new ForbiddenException("User not verified");

            String token = jwtService.generateToken(user);

            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Login successful",
                    authMapper.fromLoggedInUser(user, token)
            );
        } catch (AuthenticationException e) {
            throw new ForbiddenException(e.getMessage());
        }
    }

    @Override
    public ApiResponse<String> sendResetCode(String email) throws MessagingException {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(email + " not found")
        );
        user.setRestPassCode(generateVerificationCode());
        user.setRestPassCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        User user1 = userRepository.save(user);

        emailService.sendPasswordResetCode(email, user1.getRestPassCode());
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Password reset code sent to email",
                null
        );
    }

    @Override
    public ApiResponse<String> resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow(() ->
                new UsernameNotFoundException(request.email() + " not found")
        );

        if (user.getRestPassCode() == null) throw new BadRequestException("Invalid rest code");
        if (!user.getRestPassCode().equals(request.resetCode())) throw new BadRequestException("Invalid reset code");

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setRestPassCode(null);
        user.setRestPassCodeExpiresAt(null);

        userRepository.save(user);
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Password successfully reset",
                null
        );
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
