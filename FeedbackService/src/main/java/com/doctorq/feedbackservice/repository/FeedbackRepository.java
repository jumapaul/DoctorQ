package com.doctorq.feedbackservice.repository;

import com.doctorq.feedbackservice.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long> {

    List<FeedbackEntity> findByDoctorId(Long doctorId);

    Integer countByDoctorId(Long doctorId);
}
