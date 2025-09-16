package com.doctorq.doctorservice.service;

import com.doctorq.doctorservice.dtos.DoctorRequest;
import com.doctorq.doctorservice.entities.DoctorEntity;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.mapper.DoctorMapper;
import com.doctorq.doctorservice.repository.DoctorRepository;
import com.doctorq.doctorservice.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Override
    public ApiResponse<DoctorEntity> addDoctors(DoctorRequest request) {
        DoctorEntity doctor = doctorRepository.findByEmail(request.email()).orElseGet(() ->
                doctorRepository.save(doctorMapper.toDoctorEntity(request)));

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Doctor successfully added",
                doctor
        );
    }

    @Override
    public ApiResponse<List<DoctorEntity>> getAllDoctors() {
        List<DoctorEntity> doctors = doctorRepository.findAll();
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Doctors retrieved",
                doctors
        );
    }

    @Override
    public ApiResponse<DoctorEntity> getDoctorById(Long id) {

        DoctorEntity doctor = doctorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Doctor with id " + id + " not found")
        );
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "User successfully retrieved",
                doctor
        );
    }

    @Override
    public ApiResponse<DoctorEntity> updateDoctor(Long id, DoctorRequest request) {
        DoctorEntity doctor = doctorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Doctor with id " + id + " not found")
        );

        doctor.setFullName(request.fullName());
        doctor.setEmail(request.email());
        doctor.setSpecialization(request.specialization());
        doctor.setHospital(request.hospital());

        doctorRepository.save(doctor);

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "User updated successfully",
                doctor
        );
    }

    @Override
    public ApiResponse<String> deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Doctor successfully deleted",
                null
        );
    }
}
