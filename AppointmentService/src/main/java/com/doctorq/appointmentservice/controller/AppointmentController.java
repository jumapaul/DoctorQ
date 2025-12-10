package com.doctorq.appointmentservice.controller;

import com.doctorq.appointmentservice.dtos.AddAppointmentRequest;
import com.doctorq.appointmentservice.dtos.ApiResponse;
import com.doctorq.appointmentservice.dtos.AppointmentResponse;
import com.doctorq.appointmentservice.service.AppointmentService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponse>> addAppointment(
            @RequestBody AddAppointmentRequest request
    ) throws MessagingException {
        AppointmentResponse appointment = appointmentService.addAppointment(request);
        return ResponseEntity.ok(
                success(appointment, "Appointment added successfully")
        );
    }


    private <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<T>(
                HttpStatus.OK.value(),
                message,
                data
        );
    }
}
