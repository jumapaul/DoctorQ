package com.doctorq.feedbackservice.outer_box;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "feedback_outerbox")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackOuterBoxEntity {
    @Id
    private Long id;
    private Long doctorId;
    private Double rating;
    private Integer ratingCount;
    private Integer reviewsCount;
}
