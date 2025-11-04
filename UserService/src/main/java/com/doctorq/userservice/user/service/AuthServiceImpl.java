package com.doctorq.userservice.user.service;

import com.doctorq.userservice.config.JwtService;
import com.doctorq.userservice.exception.*;
import com.doctorq.userservice.mail.EmailService;
import com.doctorq.userservice.user.dtos.*;
import com.doctorq.userservice.user.entities.User;
import com.doctorq.userservice.user.mappers.AuthMapper;
import com.doctorq.userservice.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.OffsetDateTime;
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

    @CacheEvict(value = "getAllUsers", allEntries = true)
    @Override
    public RegisterResponse registerUser(@Valid RegisterUserDto registerUserDto) throws MessagingException {

        boolean userExists = userRepository.findByEmail(registerUserDto.email()).isPresent();

        if (userExists) throw new ConflictException("User already exists");

        User user = userRepository.save(authMapper.toUser(registerUserDto, generateVerificationCode()));

        //Send email.
        emailService.sendVerificationCode(user.getEmail(), user.getVerificationCode());

        return authMapper.fromUser(user);
    }

    @Override
    public void verifyUser(VerifyUserDto verifyUserDto) {
        User user = userRepository.findByEmail(verifyUserDto.email()).orElseThrow(() ->
                new ResourceNotFoundException("User with email " + verifyUserDto.email() + " not found")
        );

        if (user.getVerificationCode() == null) throw new ConflictException("User already verified");

        if (user.getVerificationExpiresAt().isBefore(OffsetDateTime.now()))
            throw new BadRequestException("Verification code already expired.");

        if (verifyUserDto.verificationCode().equals(user.getVerificationCode())) {
            user.setVerificationCode(null);
            user.setEnabled(true);

            userRepository.save(user);
        } else {
            throw new BadRequestException("Invalid verification code");
        }
    }

    @Override
    public void resendVerificationCode(String email) throws MessagingException {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException(email + " not found")
        );

        if (user.isEnabled()) throw new BadRequestException("User already verified");
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpiresAt(OffsetDateTime.now().plusMinutes(15));

        userRepository.save(user);

        emailService.sendVerificationCode(email, user.getVerificationCode());
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {

        User user = userRepository.findByEmail(request.email()).orElseThrow(() ->
                new ResourceNotFoundException("User with the email " + request.email() + " not found")
        );

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            String token = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            return authMapper.fromLoggedInUser(user, token, refreshToken);

        } catch (DisabledException exception) {
            throw new ForbiddenException("User not verified");
        } catch (AuthenticationException e) {
            throw new UnAuthorizedException("Invalid credentials");
        }
    }

    @Override
    public void sendResetCode(String email) throws MessagingException {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException(email + " not found")
        );
        user.setRestPassCode(generateVerificationCode());
        user.setRestPassCodeExpiresAt(OffsetDateTime.now().plusMinutes(10));

        User user1 = userRepository.save(user);

        emailService.sendPasswordResetCode(email, user1.getRestPassCode());
    }

    @Override
    public void verifyPassResetCode(VerifyPassResetCode verifyPassResetCode) {
        User user = userRepository.findByEmail(verifyPassResetCode.email()).orElseThrow(() ->
                new ResourceNotFoundException("User with email " + verifyPassResetCode.email() + " not found")
        );

        if (!user.getRestPassCode().equals(verifyPassResetCode.resetPassCode())) throw
                new BadRequestException("Invalid verification code");
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow(() ->
                new ResourceNotFoundException(request.email() + " not found")
        );

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setRestPassCode(null);
        user.setRestPassCodeExpiresAt(null);

        userRepository.save(user);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    @Override
    public RefreshTokenResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnAuthorizedException("No auth header provided");
        }

        final String refreshToken = authHeader.substring(7);

        RefreshTokenResponse refreshTokenResponse = null;
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            var userDetails = this.userRepository.findByEmail(userEmail).orElseThrow(() ->
                    new ResourceNotFoundException("User not found")
            );

            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                var accessToken = jwtService.generateToken(userDetails);

                refreshTokenResponse = new RefreshTokenResponse(
                        accessToken,
                        refreshToken
                );

//                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }

        return refreshTokenResponse;
    }
}
