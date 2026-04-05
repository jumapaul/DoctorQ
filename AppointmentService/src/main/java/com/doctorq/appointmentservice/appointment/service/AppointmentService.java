package com.doctorq.appointmentservice.appointment.service;

import com.doctorq.appointmentservice.appointment.dtos.*;
import com.doctorq.appointmentservice.appointment.entity.AppointmentEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    AppointmentResponse addAppointment(AddAppointmentRequest request, String authToken) throws MessagingException;

    AppointmentResponse approveAppointment(Long id, String token) throws MessagingException;

    AppointmentResponse cancelAppointment(Long id);

    AppointmentResponse completeAppointment(Long id);

    List<AppointmentEntity> getUserAppointmentByStatus(Long userId, AppointmentStatus status) throws JsonProcessingException;

    List<AppointmentEntity> getUserAppointmentsByDateAndStatus(Long userId, LocalDate date, AppointmentStatus status) throws JsonProcessingException;

    List<AppointmentEntity> getDoctorAppointmentByStatus(Long doctorId, AppointmentStatus status) throws JsonProcessingException;

//    void updateAppointmentStatus(Long id);
}
