package com.doctorq.doctorservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackEvent {
    private long id;
    private long doctorId;
    private double rating;
    private int ratingCount;
    private int reviewsCount;
}

//Removed avro