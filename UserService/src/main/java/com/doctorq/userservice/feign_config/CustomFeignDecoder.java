package com.doctorq.userservice.feign_config;

import com.doctorq.userservice.exception.AccessDeniedException;
import com.doctorq.userservice.exception.ServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class CustomFeignDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        HttpStatus status = HttpStatus.valueOf(response.status());

        String responseBody = extractResponseBody(response);

        return switch (status) {
            case BAD_REQUEST -> new IllegalArgumentException("Invalid request: " + responseBody);
            case UNAUTHORIZED -> new SecurityException("Unauthorized accessed");
            case FORBIDDEN -> new AccessDeniedException("Access Forbidden");
            case NOT_FOUND -> new UsernameNotFoundException("Resource not found");
            case INTERNAL_SERVER_ERROR -> new RuntimeException("----------->Internal server error " + responseBody);
            case SERVICE_UNAVAILABLE -> new ServiceUnavailableException("Service not available");
            default -> new Exception("Unexpected error: " + responseBody);
        };
    }

    private String extractResponseBody(Response response) {

        if (response.body() == null) return "No response body";

        try {
            return new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return e.getMessage();
        }
    }
}
