package com.doctorq.appointmentservice.appointment.repository;

import com.doctorq.appointmentservice.appointment.entity.HistoryOuterBoxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryOuterBoxRepository extends JpaRepository<HistoryOuterBoxEntity, Long> {
}
