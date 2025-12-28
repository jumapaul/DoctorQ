package com.doctorq.doctorservice.entities;

import com.doctorq.doctorservice.dtos.Roles;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String profilePictureUrl;
    private String hospital;
    private Double rating;
    private Integer ratingCount;
    private Integer reviewsCount;
    @Enumerated(EnumType.STRING)
    private Roles role;
    private String aboutDoctor;
    private Integer yearsOfExperience;
    private Integer numberOfPatients;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "working_hours_id", referencedColumnName = "id")
    private WorkingHours schedule;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "doctor_categories",
            joinColumns = @JoinColumn(name = "doctorId"),
            inverseJoinColumns = @JoinColumn(name = "categoryId")
    )
    @JsonIgnore
    private Set<DoctorCategoryEntity> doctorCategory = new HashSet<>();
}