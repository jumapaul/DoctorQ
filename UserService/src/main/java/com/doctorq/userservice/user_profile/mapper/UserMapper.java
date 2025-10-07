package com.doctorq.userservice.user_profile.mapper;

import com.doctorq.userservice.user.entities.User;
import com.doctorq.userservice.user_profile.dtos.UserProfile;
import com.doctorq.userservice.user_profile.dtos.UserProfileRequest;
import com.doctorq.userservice.user_profile.dtos.UserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserMapper {

    public UserResponseDto fromUser(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getRole(),
                user.getUserProfile()
        );
    }

    public UserProfile toUserProfile(UserProfileRequest request) {
        UserProfile userProfile = UserProfile.builder()
                .gender(request.gender())
                .profileUrl(request.profileUrl())
                .address(request.address())
                .dateOfBirth(request.dateOfBirth())
                .build();

        log.info("====================>userprofile: {}", userProfile);

        return userProfile;
    }
}
