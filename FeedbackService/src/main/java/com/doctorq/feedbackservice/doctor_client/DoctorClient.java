package com.doctorq.feedbackservice.doctor_client;

import com.doctorq.feedbackservice.feign_config.FeignClientConfiguration;
import com.doctorq.feedbackservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "doctor-client",
        url = "${application.config.doctor-url}",
        configuration = FeignClientConfiguration.class
)
public interface DoctorClient {

    @GetMapping("/{id}")
    ApiResponse<DoctorResponse> getDoctorById(
            @PathVariable(name = "id") Long id,
            @RequestHeader("Authorization") String authHeader
    );
}
