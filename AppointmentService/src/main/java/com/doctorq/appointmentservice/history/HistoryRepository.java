package com.doctorq.appointmentservice.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {


    Page<HistoryEntity> findAllByUserId(Pageable pageable, Long userId);
}
