package com.doctorq.appointmentservice.service;

import com.doctorq.appointmentservice.dtos.AddAppointmentRequest;
import com.doctorq.appointmentservice.dtos.AppointmentResponse;
import jakarta.mail.MessagingException;

public interface AppointmentService {

    AppointmentResponse addAppointment(AddAppointmentRequest request) throws MessagingException;
}
