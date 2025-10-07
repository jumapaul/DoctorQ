package com.doctorq.doctorservice.repository;

import com.doctorq.doctorservice.entities.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {
    Optional<DoctorEntity> findByEmail(String email);

    @Query("select d from DoctorEntity d where d.rating>=4 order by d.rating desc")
    List<DoctorEntity> getTopDoctor();
}
