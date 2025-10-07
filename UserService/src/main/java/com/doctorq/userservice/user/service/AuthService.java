package com.doctorq.userservice.user.service;

import com.doctorq.userservice.response.ApiResponse;
import com.doctorq.userservice.user.dtos.*;
import jakarta.mail.MessagingException;

public interface AuthService {

    ApiResponse<RegisterResponse> registerUser(RegisterUserDto registerUserDto) throws MessagingException;

    ApiResponse<String> verifyUser(VerifyUserDto verifyUserDto);

    ApiResponse<LoginResponse> loginUser(LoginRequest request);

    ApiResponse<String> resendVerificationCode(String email) throws MessagingException;

    ApiResponse<String> sendResetCode(String email) throws MessagingException;

    ApiResponse<String> resetPassword(ResetPasswordRequest request);

    ApiResponse<String> verifyPassResetCode(VerifyPassResetCode verifyPassResetCode);
    //send reset verification code

    // reset password

}
