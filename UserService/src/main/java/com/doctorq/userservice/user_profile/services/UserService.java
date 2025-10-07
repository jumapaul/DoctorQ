package com.doctorq.userservice.user_profile.services;

import com.doctorq.userservice.response.ApiResponse;
import com.doctorq.userservice.user_profile.dtos.UserProfileRequest;
import com.doctorq.userservice.user_profile.dtos.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Long userId);

    void deleteUser(Long userId);

    UserResponseDto addUserProfile(UserProfileRequest request, Long userId);

    UserResponseDto updateUserProfile(UserProfileRequest request, Long userId);

    String uploadProfileImage(MultipartFile multipartFile, Long userId) throws Exception;
}
