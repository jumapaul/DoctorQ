package com.doctorq.appointmentservice.appointment.service;

import com.doctorq.appointmentservice.appointment.dtos.*;
import com.doctorq.appointmentservice.appointment.entity.AppointmentEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    AppointmentResponse addAppointment(AddAppointmentRequest request, String authToken) throws MessagingException;

    AppointmentResponse approveAppointment(Long id, UpdateAppointmentRequest request) throws MessagingException;

    AppointmentResponse cancelAppointment(Long id, UpdateAppointmentRequest request) throws MessagingException;

    PaginatedResponse<AppointmentEntity> getAllAppointment(int page, int size) throws JsonProcessingException;

    List<AppointmentEntity> getAppointmentByStatus(AppointmentStatus status) throws JsonProcessingException;

    List<AppointmentEntity> getAppointmentsByDate(LocalDate date) throws JsonProcessingException;
}
