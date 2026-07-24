package com.doctorq.userservice.user.service;

import com.doctorq.userservice.exception.BadRequestException;
import com.doctorq.userservice.exception.ResourceNotFoundException;
import com.doctorq.userservice.kafka.AssignToDoctorDto;
import com.doctorq.userservice.kafka.KafkaProducer;
import com.doctorq.userservice.kafka.UserToDoctorRequestEvent;
import com.doctorq.userservice.user.Roles;
import com.doctorq.userservice.user.entities.User;
import com.doctorq.userservice.user.repository.UserRepository;
import com.doctorq.userservice.user_profile.dtos.UserProfileResponse;
import com.doctorq.userservice.user_profile.dtos.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagementServiceImpl {
    private final UserRepository userRepository;
    private final KafkaProducer kafkaProducer;

    public UserResponseDto changeUserRole(Long userId, Roles role) {
        User user = changeRole(userId, role);

        return buildResponse(user);
    }

    @Transactional
    public UserResponseDto changeRoleToDoctor(AssignToDoctorDto assignToDoctorDto) {

        User user = changeRole(assignToDoctorDto.userId(), Roles.DOCTOR);

        String fullName = user.getFirstname() + " " + user.getLastname();

        //Send event
        UserToDoctorRequestEvent event = new UserToDoctorRequestEvent(
                assignToDoctorDto.userId(),
                fullName,
                user.getEmail(),
                user.getUserProfile().getProfileUrl(),
                assignToDoctorDto.hospital(),
                assignToDoctorDto.about(),
                assignToDoctorDto.yearsOfExperience(),
                assignToDoctorDto.categories(),
                assignToDoctorDto.date(),
                assignToDoctorDto.startTime(),
                assignToDoctorDto.endTime()
        );
        log.info("-------------> Event is: {}", event);
        kafkaProducer.publish(event);
        return buildResponse(user);
    }

    private User changeRole(Long userId, Roles roles) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found")
        );

        if (user.getUserProfile() == null){
            throw new BadRequestException("Add user profile");
        }

        user.setRole(roles);

        userRepository.save(user);

        return user;
    }

    private static UserResponseDto buildResponse(User user) {

        UserProfileResponse userProfileResponse = null;
        if (user.getUserProfile() != null) {
            userProfileResponse = new UserProfileResponse(
                    user.getUserProfile().getId(),
                    user.getUserProfile().getGender(),
                    user.getUserProfile().getDateOfBirth(),
                    user.getUserProfile().getAddress(),
                    user.getUserProfile().getProfileUrl()
            );
        }

        return new UserResponseDto(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getRole(),
                userProfileResponse
        );
    }
}
