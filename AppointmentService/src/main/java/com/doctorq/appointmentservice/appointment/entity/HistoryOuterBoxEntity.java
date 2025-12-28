package com.doctorq.appointmentservice.appointment.entity;

import com.doctorq.appointmentservice.appointment.dtos.HistoryStatus;
import com.doctorq.appointmentservice.appointment.dtos.ProcessedStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class HistoryOuterBoxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long doctorId;
    private String activityName;
    private String historyStatus;
    private LocalDateTime timestamp;
    @Enumerated(EnumType.STRING)
    private ProcessedStatus processedStatus;

}
