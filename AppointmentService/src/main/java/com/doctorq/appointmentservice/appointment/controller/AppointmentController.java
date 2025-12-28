package com.doctorq.appointmentservice.appointment.controller;

import com.doctorq.appointmentservice.appointment.dtos.*;
import com.doctorq.appointmentservice.appointment.entity.AppointmentEntity;
import com.doctorq.appointmentservice.appointment.service.AppointmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<AppointmentResponse>> addAppointment(
            @RequestBody AddAppointmentRequest request,
            @RequestHeader("Authorization") String authToken
    ) throws MessagingException {
        AppointmentResponse appointment = appointmentService.addAppointment(request, authToken);
        return ResponseEntity.ok(
                success(appointment, "Appointment added successfully")
        );
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> approveAppointment(
            @PathVariable Long id,
            @RequestBody UpdateAppointmentRequest request
    ) throws MessagingException {
        AppointmentResponse appointment = appointmentService.approveAppointment(id, request);

        return ResponseEntity.ok(
                success(appointment, "Appointment approved successfully")
        );
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancelAppointment(
            @PathVariable Long id,
            @RequestBody UpdateAppointmentRequest request
    ) throws MessagingException {
        AppointmentResponse appointment = appointmentService.cancelAppointment(id, request);

        return ResponseEntity.ok(
                success(appointment, "Appointment cancelled successfully")
        );
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<AppointmentEntity>> getAllAppointment(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page
    ) throws JsonProcessingException {
        PaginatedResponse<AppointmentEntity> allAppointments = appointmentService.getAllAppointment(page, size);

        return ResponseEntity.ok(allAppointments);
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<AppointmentEntity>>> getAllAppointmentByStatus(
            @RequestParam AppointmentStatus status
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
    ) throws JsonProcessingException {
        List<AppointmentEntity> allAppointments = appointmentService.getAppointmentByStatus(status);

        return ResponseEntity.ok(success(allAppointments, "Appointments retrieved"));
    }

    @GetMapping("/date")
    public ResponseEntity<ApiResponse<List<AppointmentEntity>>> getAppointmentByDate(
            @RequestParam(name = "date") LocalDate date) throws JsonProcessingException {
        List<AppointmentEntity> appointments = appointmentService.getAppointmentsByDate(date);
        return ResponseEntity.ok(success(appointments, "Appointments retrieved"));
    }


    private <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<T>(
                HttpStatus.OK.value(),
                message,
                data
        );
    }
}
