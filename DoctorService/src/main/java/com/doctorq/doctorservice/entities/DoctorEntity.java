package com.doctorq.doctorservice.entities;

import com.doctorq.doctorservice.dtos.Roles;
import com.doctorq.doctorservice.dtos.Specialization;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String email;
    @Enumerated(EnumType.STRING)
    private Specialization specialization;
    private String hospital;
    @Enumerated(EnumType.STRING)
    private Roles role;
}