package com.doctorq.userservice.user_profile.services;

import com.doctorq.userservice.response.PaginatedResponse;
import com.doctorq.userservice.user_profile.dtos.UserProfileRequest;
import com.doctorq.userservice.user_profile.dtos.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    PaginatedResponse getAllUsers(int page, int size);

    UserResponseDto getUserById(Long userId);

    void deleteUser(Long userId);

    UserResponseDto addUserProfile(UserProfileRequest request, Long userId);

    UserResponseDto updateUserProfile(UserProfileRequest request, Long userId);

    String uploadProfileImage(MultipartFile multipartFile, Long userId) throws Exception;
}
