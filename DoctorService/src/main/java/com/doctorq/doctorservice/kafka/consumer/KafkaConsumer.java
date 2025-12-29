package com.doctorq.doctorservice.kafka.consumer;

import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.kafka.event.CompletionEvent;
import com.doctorq.doctorservice.kafka.event.FeedbackEvent;
import com.doctorq.doctorservice.repository.DoctorRepository;
import com.doctorq.doctorservice.utils.RedisUtil;
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

    private static final String PatientCountTopic = "COMPLETE_TOPIC";
    private static final String PatientCountGroup = "COMPLETE_TOPIC_GROUP";

    @KafkaListener(
            topics = RatingTopic,
            groupId = RatingGroup,
            containerFactory = "feedbackKafkaListenerContainerFactory"
    )
    public void listenToFeedbackEvent(FeedbackEvent event) {
        log.info("-------------->Consuming feedback event: {}", event);
        updateDoctorFeeds(event);
    }

    @KafkaListener(
            topics = PatientCountTopic,
            groupId = PatientCountGroup,
            containerFactory = "patientCountKafkaListenerContainerFactory"
    )
    public void listenToCompletionEvent(CompletionEvent event) {
        log.info("------------>Consuming completion event: {}", event);
        redisUtil.delete(allDoctorsCache);
        redisUtil.delete(topDoctorsCache);
        redisUtil.delete(doctorByIdCache + event.getDoctorId());
        DoctorEntity doctor = doctorRepository.findById(event.getDoctorId()).orElseThrow(() ->
                new ResourceNotFoundException("Doctor with id " + event.getDoctorId() + " not found")
        );

        doctor.setNumberOfPatients(doctor.getNumberOfPatients() + 1);
        doctorRepository.save(doctor);
    }

    private void updateDoctorFeeds(FeedbackEvent feedbackAvcEvent) {
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
    }
}
