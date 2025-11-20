package com.doctorq.favoriteservice.config;

import com.doctorq.favoriteservice.exception.AccessDeniedException;
import com.doctorq.favoriteservice.exception.ResourceNotFoundException;
import com.doctorq.favoriteservice.exception.ServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomFeignDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {

        HttpStatus status = HttpStatus.valueOf(response.status());

        String responseBody = extractResponseBody(response);

        return switch (status) {
            case BAD_REQUEST -> new IllegalArgumentException("Invalid request: " + responseBody);
            case UNAUTHORIZED -> new SecurityException("Unauthorized accessed");
            case FORBIDDEN -> new AccessDeniedException("Access Forbidden");
            case NOT_FOUND -> new ResourceNotFoundException("Item not found");
            case INTERNAL_SERVER_ERROR -> new RuntimeException("Internal server error");
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
