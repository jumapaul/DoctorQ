package com.doctorq.appointmentservice.appointment.entity;

import com.doctorq.appointmentservice.appointment.dtos.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        indexes = {
                @Index(
                        name = "idx_appointment_date_time",
                        columnList = "date,start_time,end_time"
                )
        }
)

public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String patientName;
    private Integer patientAge;
    private String patientContact;
    private String patientGender;
    @Column(length = 2000)
    private String patientDescription;
    private Long doctorId;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private AppointmentStatus appointmentStatus;
    private LocalDate date;
    private LocalTime starTime;
    private LocalTime endTime;
}
