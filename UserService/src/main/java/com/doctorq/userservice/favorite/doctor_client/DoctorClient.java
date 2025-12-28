package com.doctorq.userservice.favorite.doctor_client;

import com.doctorq.userservice.favorite.response.DoctorResponse;
import com.doctorq.userservice.feign_config.FeignClientConfiguration;
import com.doctorq.userservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "DOCTOR-SERVICE",
//        name = "doctor-service",
        url = "http://34.218.120.119:8020",
        configuration = FeignClientConfiguration.class
)
public interface DoctorClient {

    @GetMapping("/api/v1/doctors/{id}")
    ApiResponse<DoctorResponse> getDoctorById(
            @PathVariable(name = "id") Long id
    );
}
