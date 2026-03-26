package com.doctorq.doctorservice.kafka.consumer;

import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.dtos.WorkingHoursRequest;
import com.doctorq.doctorservice.dtos.response.DoctorResponse;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.kafka.event.AppointmentCompletionEvent;
import com.doctorq.doctorservice.kafka.event.UserToDoctorRequestEvent;
import com.doctorq.doctorservice.repository.DoctorRepository;
import com.doctorq.doctorservice.service.DoctorService;
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

    private final DoctorService doctorService;
    private final DoctorRepository doctorRepository;
    private final RedisUtil redisUtil;

    @KafkaListener(
            topics = "COMPLETION_TOPIC",
            groupId = "COMPLETION_GROUP"
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

    @KafkaListener(
            topics = "AssignDoctorTopic",
            groupId = "ASSIGN_DOCTOR_CONSUMER_GROUP",
            containerFactory = "assignDoctorListenerContainerFactory"
    )
    public void listenToAssignDoctorEvent(UserToDoctorRequestEvent event) {

        log.info("------------>Consuming assign to doctor event: {}", event.about());
        WorkingHoursRequest schedule = new WorkingHoursRequest(
                event.date(),
                event.startTime(),
                event.endTime()
        );
        DoctorRequest request = new DoctorRequest(
                event.userId(),
                event.fullName(),
                event.email(),
                event.profileUrl(),
                event.hospital(),
                event.categories(),
                event.about(),
                event.yearsOfExperience(),
                schedule
        );
        DoctorResponse response = doctorService.addDoctor(request);
        log.info("The add doctor response is: " + response);
    }
}
