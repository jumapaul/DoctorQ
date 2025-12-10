package com.doctorq.appointmentservice.entity;

import com.doctorq.appointmentservice.dtos.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private String date;
    private String time;
}
