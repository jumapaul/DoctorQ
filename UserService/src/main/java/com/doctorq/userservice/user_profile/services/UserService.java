package com.doctorq.userservice.user_profile.services;

import com.doctorq.userservice.response.ApiResponse;
import com.doctorq.userservice.user_profile.dtos.UserProfileRequest;
import com.doctorq.userservice.user_profile.dtos.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    ApiResponse<List<UserResponseDto>> getAllUsers();

    ApiResponse<UserResponseDto> getUserById(Long userId);

    ApiResponse<String> deleteUser(Long userId);

    ApiResponse<UserResponseDto> addUserProfile(UserProfileRequest request, Long userId);

    ApiResponse<UserResponseDto> updateUserProfile(UserProfileRequest request, Long userId);

    ApiResponse<String> uploadProfileImage(MultipartFile multipartFile, Long userId) throws Exception;
}
