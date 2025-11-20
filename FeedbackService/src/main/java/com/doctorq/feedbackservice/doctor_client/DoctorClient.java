package com.doctorq.feedbackservice.doctor_client;

import com.doctorq.feedbackservice.feign_config.FeignClientConfiguration;
import com.doctorq.feedbackservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
//        name = "DOCTOR-SERVICE",
        name = "doctorService",
        url = "http://localhost:8020",
        configuration = FeignClientConfiguration.class
)
public interface DoctorClient {

    @GetMapping("/api/v1/doctors/{id}")
    ApiResponse<DoctorResponse> getDoctorById(
            @PathVariable(name = "id") Long id
    );
}