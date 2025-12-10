package com.doctorq.appointmentservice.repository;

import com.doctorq.appointmentservice.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
}
