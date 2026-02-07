package com.doctorq.appointmentservice.appointment.feign_client;

import com.doctorq.appointmentservice.appointment.dtos.ApiResponse;
import com.doctorq.appointmentservice.appointment.dtos.DoctorResponse;
import com.doctorq.appointmentservice.appointment.dtos.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
//        name = "USER-SERVICE",
        name = "user-service",
        url = "localhost:8010",
        configuration = FeignClientConfiguration.class
)
public interface UserClient {

    @GetMapping("/api/v1/users/{id}")
    ApiResponse<UserResponse> getUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authToken
    );
}
