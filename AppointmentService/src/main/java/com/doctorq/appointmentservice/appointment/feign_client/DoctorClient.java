package com.doctorq.appointmentservice.appointment.feign_client;

import com.doctorq.appointmentservice.appointment.dtos.ApiResponse;
import com.doctorq.appointmentservice.appointment.dtos.DoctorResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "DOCTOR-SERVICE",
//        url = "http://34.218.120.119:8020",
        configuration = FeignClientConfiguration.class
)
public interface DoctorClient {

    @GetMapping("/api/v1/doctors/{id}")
    ApiResponse<DoctorResponse> getDoctorById(
            @PathVariable(name = "id") Long id
    );
}
