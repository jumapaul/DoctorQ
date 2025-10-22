package com.doctorq.userservice.user_profile.controller;

import com.doctorq.userservice.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/userService")
    public ResponseEntity<ApiResponse<String>> doctorFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(success("Doctor Service is currently unavailable. Please try again later."));
    }

    private <T> ApiResponse<T> success(String message) {
        return new ApiResponse<T>(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                message,
                null
        );
    }
}
