package com.doctorq.doctorservice.favorite;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<UserFavoritesEntity, Long> {
    Optional<UserFavoritesEntity> findByUserId(Long userId);
}
