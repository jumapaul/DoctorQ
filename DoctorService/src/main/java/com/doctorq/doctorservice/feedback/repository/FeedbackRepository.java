package com.doctorq.doctorservice.feedback.repository;

import com.doctorq.doctorservice.feedback.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {

    List<FeedbackEntity> findByDoctorId(Long doctorId);

    Integer countByDoctorId(Long doctorId);
}
