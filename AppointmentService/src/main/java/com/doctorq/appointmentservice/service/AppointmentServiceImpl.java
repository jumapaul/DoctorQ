package com.doctorq.appointmentservice.service;

import com.doctorq.appointmentservice.AppointmentMapper;
import com.doctorq.appointmentservice.dtos.AddAppointmentRequest;
import com.doctorq.appointmentservice.dtos.AppointmentResponse;
import com.doctorq.appointmentservice.entity.AppointmentEntity;
import com.doctorq.appointmentservice.mail.EmailService;
import com.doctorq.appointmentservice.repository.AppointmentRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.doctorq.appointmentservice.mail.EmailTemplate.APPOINTMENT_CREATION;
import static com.doctorq.appointmentservice.mail.EmailTemplate.DOCTOR_MAIL;


@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final EmailService emailService;

    @Transactional
    @Override
    public AppointmentResponse addAppointment(AddAppointmentRequest request) throws MessagingException {

        //send notification to email
        //send android notification
        //check if doctor exists

        AppointmentEntity entity = appointmentMapper.toAppointmentEntity(request);
        AppointmentEntity appointmentEntity = appointmentRepository.save(entity);

        AppointmentResponse response = appointmentMapper.fromAppointmentEntity(appointmentEntity);

        Map<String, Object> userVariables = new HashMap<>();
        userVariables.put("appointment", response);
        userVariables.put("doctorName", "James");
        final String userMailTemplate = APPOINTMENT_CREATION.getTemplate();
        final String doctorMailTemplate = DOCTOR_MAIL.getTemplate();


        emailService.sendAppointmentCreation("abc@gmail.com", userMailTemplate,
                userVariables, "Appointment created");
        emailService.sendAppointmentCreation("doctorabc@gmail.com", doctorMailTemplate,
                userVariables, "New appointment scheduled");

        return response;
    }
}
