package com.doctorq.favoriteservice.doctor_client;

import com.doctorq.favoriteservice.config.FeignClientConfiguration;
import com.doctorq.favoriteservice.response.ApiResponse;
import com.doctorq.favoriteservice.response.DoctorResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
//        name = "DOCTOR-SERVICE",
        name = "doctor-service",
        url = "http://35.88.85.89:8020",
        configuration = FeignClientConfiguration.class
)
public interface DoctorClient {

    @GetMapping("/api/v1/doctors/{id}")
    ApiResponse<DoctorResponse> getDoctorById(
            @PathVariable(name = "id") Long id
    );
}
