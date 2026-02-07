package com.doctorq.doctorservice.feedback.controller;

import com.doctorq.doctorservice.feedback.dtos.FeedbackRequest;
import com.doctorq.doctorservice.feedback.entity.FeedbackEntity;
import com.doctorq.doctorservice.feedback.response.ApiResponse;
import com.doctorq.doctorservice.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<FeedbackEntity>> addFeedback(
            @RequestBody FeedbackRequest request,
            @RequestHeader("Authorization") String token
    ) {
        FeedbackEntity response = feedbackService.addFeedback(request, token);
        return ResponseEntity.ok(success(response, "Feedback added successfully"));
    }

    @GetMapping("/{doctorId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<FeedbackEntity>>> getAllFeedbacks(
            @PathVariable(name = "doctorId") Long doctorId
    ) {
        List<FeedbackEntity> allFeedback = feedbackService.getAllDoctorFeedback(doctorId);
        return ResponseEntity.ok(success(allFeedback, "All feedback retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(
            @PathVariable(name = "id") Long id
    ) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.ok(success(null, "Feedback deleted successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<FeedbackEntity>> updateFeedback(
            @PathVariable(name = "id") Long id,
            @RequestBody FeedbackRequest request
    ) {
        FeedbackEntity response = feedbackService.updateFeedback(id, request);
        return ResponseEntity.ok(success(response, "Feedback updated successfully"));
    }

    private <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<T>(
                HttpStatus.OK.value(),
                message,
                data
        );
    }
}
