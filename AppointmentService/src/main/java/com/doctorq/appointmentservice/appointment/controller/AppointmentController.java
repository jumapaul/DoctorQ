package com.doctorq.appointmentservice.appointment.controller;

import com.doctorq.appointmentservice.appointment.dtos.*;
import com.doctorq.appointmentservice.appointment.service.AppointmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointmentById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) throws JsonProcessingException {
        AppointmentResponse appointmentResponse = appointmentService.getAppointmentById(id, token);

        return ResponseEntity.ok(
                success(appointmentResponse, "Appointment retrieved successfully")
        );
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DOCTOR')")
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
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> approveAppointment(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) throws MessagingException {
        AppointmentResponse appointment = appointmentService.approveAppointment(id, token);

        return ResponseEntity.ok(
                success(appointment, "Appointment approved successfully")
        );
    }

    @PostMapping("/cancel/{id}")
    @PreAuthorize("hasAnyRole('USER', 'DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancelAppointment(
            @PathVariable Long id
    ) {
        AppointmentResponse appointment = appointmentService.cancelAppointment(id);

        return ResponseEntity.ok(
                success(appointment, "Appointment cancelled successfully")
        );
    }

    @GetMapping("/complete/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> completeAppointment(
            @PathVariable Long id
    ) {
        AppointmentResponse appointment = appointmentService.completeAppointment(id);

        return ResponseEntity.ok(
                success(appointment, "Appointment completed successfully")
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getUserAppointmentByStatus(
            @PathVariable Long userId,
            @RequestParam AppointmentStatus status,
            @RequestHeader("Authorization") String token

    ) throws JsonProcessingException {
        List<AppointmentResponse> allAppointments = appointmentService.getUserAppointmentByStatus(userId, status, token);

        return ResponseEntity.ok(success(allAppointments, "Appointments retrieved"));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getDoctorAppointmentByStatus(
            @PathVariable Long doctorId,
            @RequestParam AppointmentStatus status,
            @RequestHeader("Authorization") String token

    ) throws JsonProcessingException {
        List<AppointmentResponse> allAppointments = appointmentService.getDoctorAppointmentByStatus(doctorId, status, token);

        return ResponseEntity.ok(success(allAppointments, "Appointments retrieved"));
    }

    @GetMapping("/appointByDate/{userId}/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointmentByDate(
            @PathVariable Long userId,
            @RequestParam(name = "date") LocalDate date,
            @RequestParam(name = "status") AppointmentStatus status,
            @RequestHeader("Authorization") String token
    ) throws JsonProcessingException {
        List<AppointmentResponse> appointments = appointmentService.getUserAppointmentsByDateAndStatus(userId, date, status, token);
        return ResponseEntity.ok(success(appointments, "Appointments retrieved"));
    }


    private <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(
                message,
                data
        );
    }
}
