package com.doctorq.userservice.user_profile.services;

import com.doctorq.userservice.exception.BadRequestException;
import com.doctorq.userservice.response.ApiResponse;
import com.doctorq.userservice.user.entities.User;
import com.doctorq.userservice.user.repository.UserRepository;
import com.doctorq.userservice.user_profile.UserProfileRepository;
import com.doctorq.userservice.user_profile.dtos.UserProfile;
import com.doctorq.userservice.user_profile.dtos.UserProfileRequest;
import com.doctorq.userservice.user_profile.dtos.UserResponseDto;
import com.doctorq.userservice.user_profile.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final UserMapper mapper;

    @Override
    public ApiResponse<List<UserResponseDto>> getAllUsers() {
        List<User> users = userRepository.findAll();


        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Users retrieved successfully",
                users.stream().map(mapper::fromUser).collect(Collectors.toList())
        );
    }

    @Override
    public ApiResponse<UserResponseDto> addUserProfile(UserProfileRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found")
        );

        if (user.getUserProfile() != null) {
            throw new BadRequestException("User profile already added update the existing");
        }

        if (!user.isEnabled()) {
            throw new BadRequestException("Verify user account to proceed");
        }

        UserProfile userProfile = mapper.toUserProfile(request);

        user.setUserProfile(userProfile);
        userProfile.setUser(user);
        User savedUser = userRepository.save(user);
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "User profile added successfully",
                mapper.fromUser(savedUser)
        );
    }

    @Override
    public ApiResponse<UserResponseDto> updateUserProfile(UserProfileRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found")
        );

        UserProfile userProfile = user.getUserProfile();
        if (userProfile != null) {
            userProfile.setGender(request.gender());
            userProfile.setDateOfBirth(request.dateOfBirth());
            userProfile.setAddress(request.address());
            userProfile.setProfileUrl(request.profileUrl());
        } else {
            throw new UsernameNotFoundException("User profile not found");
        }

        userRepository.save(user);
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "User profile added successfully",
                mapper.fromUser(user)
        );
    }

    @Override
    public ApiResponse<UserResponseDto> getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User with id " + userId + " not found")
        );

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "User retrieved successfully",
                mapper.fromUser(user)
        );
    }

    @Override
    public ApiResponse<String> deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User with id " + userId + " not found")
        );

        userRepository.delete(user);
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "User successfully deleted",
                null
        );
    }
}
