package com.doctorq.favoriteservice.repository;

import com.doctorq.favoriteservice.entity.FavoritesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteDoctorRepository extends JpaRepository<FavoritesEntity, Long> {

    Page<FavoritesEntity> findAllByUserId(Long id, Pageable pageable);

    boolean existsByUserIdAndDoctorId(Long userId, Long doctorId);
}
