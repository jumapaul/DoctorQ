package com.doctorq.doctorservice.repository;

import com.doctorq.doctorservice.entities.DoctorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {
    Optional<DoctorEntity> findByEmail(String email);

    @Query("select d from DoctorEntity d where d.rating>=4 order by d.rating desc")
    Page<DoctorEntity> getTopDoctor(Pageable page);

    @Query("""
            select d from DoctorEntity d
            join d.doctorCategory c
            where c.id = :categoryId and
            d.rating>=4 order by d.rating desc
           """)
    Page<DoctorEntity> getCategoryTopDoctor(Long categoryId, Pageable page);

    @Query("select d from DoctorEntity d where lower(d.fullName) LIKE lower(CONCAT('%', :name, '%'))")
    Page<DoctorEntity> findAllByFullName(@Param("name") String name, Pageable page);
}
