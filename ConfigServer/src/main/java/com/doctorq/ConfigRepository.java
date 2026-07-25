package com.doctorq;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigRepository extends JpaRepository<ConfigEntity, Long> {

    List<ConfigEntity> findAllByServiceNameIn(List<String> serviceName);
}
