package com.doctorq.appointmentservice.kafka.event;

import com.doctorq.appointmentservice.appointment.dtos.ProcessedStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HistoryEvent {
    private Long id;
    private Long userId;
    private Long doctorId;
    private String activityName;
    private String historyStatus;
    private LocalDateTime timestamp;
    private ProcessedStatus processedStatus;
}
