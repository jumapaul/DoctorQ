package com.doctorq.feedbackservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FeedbackEvent {
    private long id;
    private long doctorId;
    private double rating;
    private int ratingCount;
    private int reviewsCount;
}
