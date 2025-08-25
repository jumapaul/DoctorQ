package com.doctorq.userservice.user.controller;

import com.doctorq.userservice.response.ApiResponse;
import com.doctorq.userservice.user.dtos.*;
import com.doctorq.userservice.user.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> registerUser(
            @RequestBody @Valid RegisterUserDto registerUserDto) throws MessagingException {
        return ResponseEntity.ok(userService.registerUser(registerUserDto));
    }

    @PostMapping("/verifyUser")
    public ResponseEntity<ApiResponse<String>> verifyUser(
            @RequestBody VerifyUserDto verifyUserDto
    ) {
        return ResponseEntity.ok(userService.verifyUser(verifyUserDto));
    }

    @PostMapping("/loginUser")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(
            @RequestBody LoginRequest loginRequest
    ) {
        return ResponseEntity.ok(userService.loginUser(loginRequest));
    }

    @GetMapping("/resendVerificationCode")
    public ResponseEntity<ApiResponse<String>> resendVerificationCode(
            @RequestParam(name = "email") String email
    ) throws MessagingException {
        return ResponseEntity.ok(userService.resendVerificationCode(email));
    }

    @GetMapping("/sendResetCode")
    public ResponseEntity<ApiResponse<String>> sendResetCode(
            @RequestParam(name = "email") String email
    ) throws MessagingException {
        return ResponseEntity.ok(userService.sendResetCode(email));
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {
        return ResponseEntity.ok(userService.resetPassword(request));
    }
}
