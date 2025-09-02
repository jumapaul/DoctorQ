package com.doctorq.userservice.user_profile.controller;

import com.doctorq.userservice.response.ApiResponse;
import com.doctorq.userservice.user.entities.User;
import com.doctorq.userservice.user_profile.dtos.UserProfileRequest;
import com.doctorq.userservice.user_profile.dtos.UserResponseDto;
import com.doctorq.userservice.user_profile.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(
            @PathVariable(name = "userId") Long userId
    ) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable(name = "userId") Long userId
    ) {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }

    @PostMapping("/userProfile/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> addUserProfile(
            @RequestBody UserProfileRequest request,
            @PathVariable(name = "userId") Long userId
    ) {
        return ResponseEntity.ok(userService.addUserProfile(request, userId));
    }

    @PutMapping("/updateProfile/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserProfile(
            @RequestBody UserProfileRequest request,
            @PathVariable(name = "userId") Long userId
    ) {
        return ResponseEntity.ok(userService.updateUserProfile(request, userId));
    }

    @PostMapping("/uploadProfileImage/{userId}")
    public ResponseEntity<ApiResponse<String>> upload(
            @RequestParam("file") MultipartFile multipartFile,
            @PathVariable(name = "userId") Long userId
    ) throws Exception {
        return ResponseEntity.ok(userService.uploadProfileImage(multipartFile, userId));
    }

}
