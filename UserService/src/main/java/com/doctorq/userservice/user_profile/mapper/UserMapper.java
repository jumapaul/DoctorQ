package com.doctorq.userservice.user_profile.mapper;

import com.doctorq.userservice.user.entities.User;
import com.doctorq.userservice.user_profile.dtos.UserProfile;
import com.doctorq.userservice.user_profile.dtos.UserProfileRequest;
import com.doctorq.userservice.user_profile.dtos.UserProfileResponse;
import com.doctorq.userservice.user_profile.dtos.UserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserMapper {

    public UserResponseDto fromUser(User user) {
        UserProfile profile = user.getUserProfile();

        return new UserResponseDto(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getRole(),
                toUserProfileResponse(profile)
        );
    }

    public UserProfile toUserProfile(UserProfileRequest request) {
        return UserProfile.builder()
                .gender(request.gender())
                .profileUrl(request.profileUrl())
                .address(request.address())
                .dateOfBirth(request.dateOfBirth())
                .build();
    }

    public UserProfileResponse toUserProfileResponse(UserProfile userProfile) {
        return Optional.ofNullable(userProfile)
                .map(p -> new UserProfileResponse(
                        p.getId(),
                        p.getGender(),
                        p.getDateOfBirth(),
                        p.getAddress(),
                        p.getProfileUrl()
                )).orElse(null);
    }
}
