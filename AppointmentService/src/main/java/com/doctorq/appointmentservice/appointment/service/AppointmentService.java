package com.doctorq.appointmentservice.appointment.service;

import com.doctorq.appointmentservice.appointment.dtos.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    AppointmentResponse addAppointment(AddAppointmentRequest request, String authToken) throws MessagingException;

    AppointmentResponse approveAppointment(Long id, String token) throws MessagingException;

    AppointmentResponse cancelAppointment(Long id);

    AppointmentResponse completeAppointment(Long id);

    List<AppointmentResponse> getUserAppointmentByStatus(Long userId, AppointmentStatus status, String token) throws JsonProcessingException;

    List<AppointmentResponse> getUserAppointmentsByDateAndStatus(Long userId, LocalDate date, AppointmentStatus status, String token) throws JsonProcessingException;

    List<AppointmentResponse> getDoctorAppointmentByStatus(Long doctorId, AppointmentStatus status, String token) throws JsonProcessingException;

    AppointmentResponse getAppointmentById(Long id, String token) throws JsonProcessingException;
}
