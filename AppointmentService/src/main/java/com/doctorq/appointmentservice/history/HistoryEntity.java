package com.doctorq.appointmentservice.history;

import com.doctorq.appointmentservice.appointment.dtos.HistoryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class HistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long doctorId;
    @Enumerated(EnumType.STRING)
    private HistoryStatus historyStatus;
    @CreatedDate
    private LocalDateTime timestamp;
}
