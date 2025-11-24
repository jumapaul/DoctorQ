package com.doctorq.doctorservice.kafka;

import lombok.Data;

@Data
public class FeedbackEvent {
    private long id;
    private long doctorId;
    private double rating;
    private int ratingCount;
    private int reviewsCount;
}
