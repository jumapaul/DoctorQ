package com.doctorq.userservice.favorite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "FavoriteDoctors")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoritesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long doctorId;
    private OffsetDateTime createdAt;
}
