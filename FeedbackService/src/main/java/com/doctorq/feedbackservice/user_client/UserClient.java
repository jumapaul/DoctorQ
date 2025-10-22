package com.doctorq.feedbackservice.user_client;

import com.doctorq.feedbackservice.feign_config.FeignClientConfiguration;
import com.doctorq.feedbackservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

@FeignClient(
        name = "USER-SERVICE",
        configuration = FeignClientConfiguration.class
)
public interface UserClient {

    @GetMapping("/api/v1/users/{userId}")
    ApiResponse<UserResponseDto> getUserById(
            @PathVariable(name = "userId") Long userId,
            @RequestHeader("Authorization") String authHeader
    );
}
