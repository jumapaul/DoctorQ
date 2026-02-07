package com.doctorq.userservice.user_profile.controller;

import com.doctorq.userservice.response.ApiResponse;
import com.doctorq.userservice.response.PaginatedResponse;
import com.doctorq.userservice.user_profile.dtos.UserProfileRequest;
import com.doctorq.userservice.user_profile.dtos.UserResponseDto;
import com.doctorq.userservice.user_profile.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Environment environment;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PaginatedResponse<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws JsonProcessingException {

        PaginatedResponse<UserResponseDto> responseDtoList = userService.getAllUsers(page, size);
        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'DOCTOR')")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(
            @PathVariable(name = "userId") Long userId
    ) throws JsonProcessingException {
        UserResponseDto responseDto = userService.getUserById(userId);
        return ResponseEntity.ok(response(responseDto));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable(name = "userId") Long userId
    ) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(
                response(null)
        );
    }

    @PostMapping("/userProfile/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> addUserProfile(
            @RequestBody UserProfileRequest request,
            @PathVariable(name = "userId") Long userId
    ) {
        UserResponseDto responseDto = userService.addUserProfile(request, userId);
        return ResponseEntity.ok(response(responseDto));
    }

    @PutMapping("/updateProfile/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserProfile(
            @RequestBody UserProfileRequest request,
            @PathVariable(name = "userId") Long userId
    ) {
        UserResponseDto userResponseDto = userService.updateUserProfile(request, userId);
        return ResponseEntity.ok(response(userResponseDto));
    }

    @PostMapping("/uploadProfileImage/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> upload(
            @RequestParam("file") MultipartFile multipartFile,
            @PathVariable(name = "userId") Long userId
    ) throws Exception {
        String url = userService.uploadProfileImage(multipartFile, userId);
        return ResponseEntity.ok(response(url));
    }

    private <T> ApiResponse<T> response(T data) {
        return new ApiResponse<T>(
                HttpStatus.OK.value(),
                "Success",
                data
        );
    }
}
