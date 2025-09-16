package com.doctorq.doctorservice.mapper;

import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.dtos.Roles;
import com.doctorq.doctorservice.entities.DoctorEntity;
import org.springframework.stereotype.Service;

@Service
public class DoctorMapper {

    public DoctorEntity toDoctorEntity(DoctorRequest request) {
        return DoctorEntity.builder()
                .fullName(request.fullName())
                .email(request.email())
                .specialization(request.specialization())
                .hospital(request.hospital())
                .role(Roles.DOCTOR)
                .build();
    }
}
