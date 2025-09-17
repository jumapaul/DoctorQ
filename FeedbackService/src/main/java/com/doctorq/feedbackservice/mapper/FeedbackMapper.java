package com.doctorq.feedbackservice.mapper;

import com.doctorq.feedbackservice.dtos.FeedbackRequest;
import com.doctorq.feedbackservice.entity.FeedbackEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FeedbackMapper {

    public FeedbackEntity toFeedback(FeedbackRequest request, String username) {
        return FeedbackEntity.builder()
                .doctorId(request.doctorId())
                .userId(request.userId())
                .review(request.review())
                .rating(request.rating())
                .username(username)
                .time(LocalDateTime.now())
                .build();
    }
}
