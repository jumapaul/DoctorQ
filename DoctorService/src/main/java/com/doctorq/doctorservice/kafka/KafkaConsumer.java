package com.doctorq.doctorservice.kafka;

import com.doctorq.doctorservice.dtos.DoctorResponse;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.repository.DoctorRepository;
import com.doctorq.doctorservice.service.DoctorService;
import com.doctorq.doctorservice.utils.RedisUtil;
import com.doctorq.feedbackservice.kafka.FeedbackAvcEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.doctorq.doctorservice.utils.Constants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final DoctorRepository doctorRepository;
    private final RedisUtil redisUtil;

    private static final String RatingTopic = "RATING_TOPIC";
    private static final String RatingGroup = "RATING_GROUP";

    @KafkaListener(topics = RatingTopic, groupId = RatingGroup)
    public void listen(FeedbackAvcEvent event) {
        log.info("-------------->Consuming: {}", event);
        updateDoctorFeeds(event);
    }

    private void updateDoctorFeeds(FeedbackAvcEvent feedbackAvcEvent) {
        redisUtil.delete(allDoctorsCache);
        redisUtil.delete(topDoctorsCache);
        redisUtil.delete(doctorByIdCache + feedbackAvcEvent.getDoctorId());
        DoctorEntity doctor = doctorRepository.findById(feedbackAvcEvent.getDoctorId()).orElseThrow(() ->
                new ResourceNotFoundException("Doctor with id " + feedbackAvcEvent.getDoctorId() + " not found")
        );

        doctor.setRating(feedbackAvcEvent.getRating());
        doctor.setReviewsCount(feedbackAvcEvent.getReviewsCount());
        doctor.setRatingCount(feedbackAvcEvent.getRatingCount());

        doctorRepository.save(doctor);

        log.info("------------->Updated doctor is: {}", doctor);
    }
}
