package com.doctorq.feedbackservice.controller;

import com.doctorq.feedbackservice.dtos.FeedbackRequest;
import com.doctorq.feedbackservice.entity.FeedbackEntity;
import com.doctorq.feedbackservice.response.ApiResponse;
import com.doctorq.feedbackservice.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<ApiResponse<FeedbackEntity>> addFeedback(
            @RequestBody FeedbackRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(feedbackService.addFeedback(request, authHeader));
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<List<FeedbackEntity>>> getAllFeedbacks(
            @PathVariable(name = "doctorId") Long doctorId
    ) {
        return ResponseEntity.ok(feedbackService.getAllDoctorFeedback(doctorId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteFeedback(
            @PathVariable(name = "id") Long id
    ) {
        return ResponseEntity.ok(feedbackService.deleteFeedback(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FeedbackEntity>> updateFeedback(
            @PathVariable(name = "id") Long id,
            @RequestBody FeedbackRequest request
    ) {
        return ResponseEntity.ok(feedbackService.updateFeedback(id, request));
    }

}
