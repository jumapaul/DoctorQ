package com.doctorq.userservice.user.service;

import com.doctorq.userservice.response.ApiResponse;
import com.doctorq.userservice.user.dtos.*;
import jakarta.mail.MessagingException;

public interface AuthService {

    RegisterResponse registerUser(RegisterUserDto registerUserDto) throws MessagingException;

    void verifyUser(VerifyUserDto verifyUserDto);

    LoginResponse loginUser(LoginRequest request);

    void resendVerificationCode(String email) throws MessagingException;

    void sendResetCode(String email) throws MessagingException;

    void resetPassword(ResetPasswordRequest request);

    void verifyPassResetCode(VerifyPassResetCode verifyPassResetCode);
}
