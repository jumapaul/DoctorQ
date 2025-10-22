package com.doctorq.gatewayservice.controller;

import com.doctorq.gatewayservice.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @RequestMapping(value = "/fallback/userService", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<ApiResponse<String>> userFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(success("User Service is currently unavailable. Please try again later."));
    }

    @GetMapping("/fallback/doctorService")
    public ResponseEntity<ApiResponse<String>> doctorFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(success("Doctor Service is currently unavailable. Please try again later."));
    }

    @GetMapping("/fallback/feedbackService")
    public ResponseEntity<ApiResponse<String>> feedbackFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(success("Feedback Service is currently unavailable. Please try again later."));
    }

    private <T> ApiResponse<T> success(String message) {
        return new ApiResponse<T>(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                message,
                null
        );
    }
}
