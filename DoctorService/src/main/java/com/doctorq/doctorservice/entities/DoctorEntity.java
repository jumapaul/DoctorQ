package com.doctorq.doctorservice.entities;

import com.doctorq.doctorservice.dtos.Roles;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String email;
    private String hospital;
    @Enumerated(EnumType.STRING)
    private Roles role;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "doctor_categories",
            joinColumns = @JoinColumn(name = "doctorId"),
            inverseJoinColumns = @JoinColumn(name = "courseId")
    )
    @JsonManagedReference
    private Set<DoctorCategoryEntity> doctorCategory = new HashSet<>();
}