package com.doctorq.appointmentservice.appointment.repository;

import com.doctorq.appointmentservice.appointment.dtos.AppointmentStatus;
import com.doctorq.appointmentservice.appointment.entity.AppointmentEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    List<AppointmentEntity> findAllByUserIdAndAppointmentStatus(Long userId, AppointmentStatus status);

    List<AppointmentEntity> findAllByDoctorIdAndAppointmentStatus(Long doctorId, AppointmentStatus status);

    List<AppointmentEntity> findAllByDateAndAppointmentStatus(LocalDate date, AppointmentStatus status);

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

    @Query("""
            select a from AppointmentEntity a
            where a.appointmentStatus = :status
            and (
            a.date <:today
            or (a.date = :today and a.starTime <:now)
            )
            """)
    List<AppointmentEntity> findExpiredScheduledAppointments(
            @Param("today") LocalDate today,
            @Param("now") LocalTime now,
            @Param("status") AppointmentStatus status
    );

    @Query("""
            select a from AppointmentEntity a
            where a.appointmentStatus = :status
            and a.date = :today
            and a.starTime between :now and :endTime
            """)
    List<AppointmentEntity> findAppointmentsInNext30Minutes(
            LocalDate today,
            LocalTime now,
            LocalTime endTime,
            AppointmentStatus status
    );
}
