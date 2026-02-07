package com.doctorq.doctorservice.kafka.consumer;

import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.kafka.event.AppointmentCompletionEvent;
//import com.doctorq.doctorservice.kafka.event.FeedbackEvent;
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

    private static final String completionTopic = "COMPLETION_TOPIC";
    private static final String completionGroup = "COMPLETION_GROUP";


    @KafkaListener(
            topics = completionTopic,
            groupId = completionGroup
    )
    public void listenToCompletionEvent(AppointmentCompletionEvent event) {
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
}
