package com.doctorq.userservice.user.controller;

import com.doctorq.userservice.response.ApiResponse;
import com.doctorq.userservice.user.dtos.*;
import com.doctorq.userservice.user.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> registerUser(
            @RequestBody @Valid RegisterUserDto registerUserDto) throws MessagingException {
        RegisterResponse response = userService.registerUser(registerUserDto);

        return ResponseEntity.ok(
                response(response, registerUserDto.email() + " registered successfully")
        );
    }

    @PostMapping("/verifyUser")
    public ResponseEntity<ApiResponse<Void>> verifyUser(
            @RequestBody VerifyUserDto verifyUserDto
    ) {
        userService.verifyUser(verifyUserDto);
        return ResponseEntity.ok(response(null, "User verified successfully"));
    }

    @PostMapping("/loginUser")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(
            @RequestBody LoginRequest loginRequest
    ) {
        LoginResponse response = userService.loginUser(loginRequest);
        return ResponseEntity.ok(response(response, "Login successful"));
    }

    @GetMapping("/resendVerificationCode")
    public ResponseEntity<ApiResponse<String>> resendVerificationCode(
            @RequestParam(name = "email") String email
    ) throws MessagingException {
        userService.resendVerificationCode(email);
        return ResponseEntity.ok(response(null, "Verification code sent to email"));
    }

    @GetMapping("/sendResetCode")
    public ResponseEntity<ApiResponse<String>> sendResetCode(
            @RequestParam(name = "email") String email
    ) throws MessagingException {
        userService.sendResetCode(email);
        return ResponseEntity.ok(
                response(null, "Password reset code sent to email")
        );
    }

    @PostMapping("/verifyPasswordResetCode")
    public ResponseEntity<ApiResponse<String>> verifyPassResetCode(
            @RequestBody VerifyPassResetCode verifyPassResetCode
    ) {
        userService.verifyPassResetCode(verifyPassResetCode);
        return ResponseEntity.ok(response(null, "Reset code verified"));
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {
        userService.resetPassword(request);
        return ResponseEntity.ok(response(null, "Password successfully reset"));
    }

    private <T> ApiResponse<T> response(T data, String message) {
        return new ApiResponse<T>(
                HttpStatus.OK.value(),
                message,
                data
        );
    }
}
