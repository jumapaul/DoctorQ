package com.doctorq.feedbackservice.mapper;

import com.doctorq.feedbackservice.dtos.FeedbackRequest;
import com.doctorq.feedbackservice.entity.FeedbackEntity;
import com.doctorq.feedbackservice.outer_box.FeedbackOuterBoxEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Service
public class FeedbackMapper {

    public FeedbackEntity toFeedback(FeedbackRequest request) {
        return FeedbackEntity.builder()
                .doctorId(request.doctorId())
                .userId(request.userId())
                .review(request.review())
                .username(request.username())
                .rating(request.rating())
                .time(OffsetDateTime.now())
                .build();
    }

    public FeedbackOuterBoxEntity toFeedbackOuterBox(
            FeedbackEntity savedFeed,
            Double average, Integer ratingCount,
            Integer reviewsCount) {
        return FeedbackOuterBoxEntity.builder()
                .id(savedFeed.getId())
                .doctorId(savedFeed.getDoctorId())
                .rating(average)
                .ratingCount(ratingCount)
                .reviewsCount(reviewsCount)
                .build();
    }
}
