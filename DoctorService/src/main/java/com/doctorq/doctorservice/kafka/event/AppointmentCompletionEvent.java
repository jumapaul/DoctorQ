package com.doctorq.doctorservice.kafka.event;

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