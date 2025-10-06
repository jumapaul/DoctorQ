package com.doctorq.doctorservice.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DoctorCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Integer doctorsCount = 0;
    private String categoryIcon;
    @ManyToMany(mappedBy = "doctorCategory", fetch = FetchType.LAZY)
    private Set<DoctorEntity> doctors = new HashSet<>();
}
