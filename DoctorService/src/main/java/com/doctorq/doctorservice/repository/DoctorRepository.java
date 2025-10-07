package com.doctorq.doctorservice.repository;

import com.doctorq.doctorservice.entities.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {
    Optional<DoctorEntity> findByEmail(String email);
}
