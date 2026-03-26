package com.doctorq.doctorservice.feign_client;

import com.doctorq.doctorservice.exception.ForbiddenException;
import com.doctorq.doctorservice.exception.ResourceNotFoundException;
import com.doctorq.doctorservice.exception.ServiceUnavailableException;
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
            case NOT_FOUND -> new ResourceNotFoundException("Doctor not found");
            case SERVICE_UNAVAILABLE -> new ServiceUnavailableException("Service not available");
            case UNAUTHORIZED -> new SecurityException("Unauthorized accessed");
            case FORBIDDEN -> new ForbiddenException("Access Forbidden");
            default -> new Exception("Unexpected error");
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
