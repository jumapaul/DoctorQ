package com.doctorq.doctorservice.feign_client;

import com.doctorq.doctorservice.dtos.response.ApiResponse;
import com.doctorq.doctorservice.dtos.response.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "USER-SERVICE",
        configuration = FeignClientConfiguration.class
)
public interface UserClient {

    @GetMapping("/api/v1/manage/user/{userId}")
    ApiResponse<UserResponseDto> assignUserRole(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String token
    );
}
