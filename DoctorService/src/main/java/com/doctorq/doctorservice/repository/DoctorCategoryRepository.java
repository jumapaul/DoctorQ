package com.doctorq.doctorservice.repository;

import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorCategoryRepository extends JpaRepository<DoctorCategoryEntity, Long> {

    Optional<DoctorCategoryEntity> findByName(String name);
}
