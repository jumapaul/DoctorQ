package com.doctorq.doctorservice.repository;

import com.doctorq.doctorservice.entities.DoctorCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DoctorCategoryRepository extends JpaRepository<DoctorCategoryEntity, Long> {

    Optional<DoctorCategoryEntity> findByName(String name);

    @Modifying
    @Query("UPDATE DoctorCategoryEntity c SET c.doctorsCount = c.doctorsCount + 1 WHERE c.id = :categoryId")
    void incrementDoctorCount(@Param("categoryId") Long categoryId);
}
