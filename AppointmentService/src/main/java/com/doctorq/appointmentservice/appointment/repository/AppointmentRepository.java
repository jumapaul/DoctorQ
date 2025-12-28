package com.doctorq.appointmentservice.appointment.repository;

import com.doctorq.appointmentservice.appointment.dtos.AppointmentStatus;
import com.doctorq.appointmentservice.appointment.entity.AppointmentEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    List<AppointmentEntity> findAllByAppointmentStatus(AppointmentStatus appointmentStatus);

    List<AppointmentEntity> findAllByDate(LocalDate date);

//    Optional<AppointmentEntity> findByDateAndStarTime(LocalDate date, LocalTime time);

    @Query("""
            select a from AppointmentEntity a
            where a.date = :date
            and :startTime < a.endTime
            and :endTime > a.starTime
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AppointmentEntity> findOverlappingAppointment(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

//    @Param("date") LocalDate date,
//    @Param("startTime") LocalTime startTime,
//    @Param("endTime") LocalTime endTime
}
