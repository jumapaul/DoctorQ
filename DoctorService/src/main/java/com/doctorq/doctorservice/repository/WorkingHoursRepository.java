package com.doctorq.doctorservice.repository;

import com.doctorq.doctorservice.entities.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {
}
