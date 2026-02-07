package com.doctorq.appointmentservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppointmentCompletionEvent {
    private Long id;
    private Long doctorId;
    private String message;
}
