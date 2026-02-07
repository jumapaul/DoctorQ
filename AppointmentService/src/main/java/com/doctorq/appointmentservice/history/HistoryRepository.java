package com.doctorq.appointmentservice.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {

    Page<HistoryEntity> findAllByUserId(Pageable pageable, Long userId);
}
