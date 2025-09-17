package com.doctorq.feedbackservice.repository;

import com.doctorq.feedbackservice.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {

    List<FeedbackEntity> findByDoctorId(Long doctorId);
}
