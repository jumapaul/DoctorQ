package com.doctorq.doctorservice.feedback.mapper;

import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.feedback.dtos.FeedbackRequest;
import com.doctorq.doctorservice.feedback.entity.FeedbackEntity;
import org.springframework.stereotype.Service;

@Service
public class FeedbackMapper {

    public FeedbackEntity toFeedback(FeedbackRequest request, DoctorEntity doctor) {
        return FeedbackEntity.builder()
                .doctorId(request.doctorId())
                .userId(request.userId())
                .review(request.review())
                .username(request.username())
                .rating(request.rating())
                .doctor(doctor)
                .build();
    }
}
