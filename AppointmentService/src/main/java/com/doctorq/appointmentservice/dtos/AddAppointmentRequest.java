package com.doctorq.appointmentservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddAppointmentRequest(
        @NotBlank(message = "Patient name is required")
        String patientName,
        @NotNull(message = "Patient age is required")
        Integer patientAge,
        @NotBlank(message = "Patient contact is required")
        String patientContact,
        String patientGender,
        @NotBlank(message = "A problem description is required")
        String patientDescription,
        @NotNull(message = "Doctor id is required")
        Long doctorId,
        Long userId,
        @NotBlank(message = "Date is required")
        String date,
        @NotBlank(message = "Time is required")
        String time
) {
}
