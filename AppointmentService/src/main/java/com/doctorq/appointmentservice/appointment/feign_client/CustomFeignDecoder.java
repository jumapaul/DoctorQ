package com.doctorq.appointmentservice.appointment.feign_client;

import com.doctorq.appointmentservice.appointment.exception.AccessDeniedException;
import com.doctorq.appointmentservice.appointment.exception.ResourceNotFoundException;
import com.doctorq.appointmentservice.appointment.exception.ServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class CustomFeignDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        String responseBody = extractResponseBody(response);
        return switch (status) {
            case BAD_REQUEST -> new IllegalArgumentException("Invalid request: " + responseBody);
            case NOT_FOUND -> new ResourceNotFoundException("Resource not found");
            case SERVICE_UNAVAILABLE -> new ServiceUnavailableException("Service not available");
            case UNAUTHORIZED -> new SecurityException("Unauthorized accessed");
            case FORBIDDEN -> new AccessDeniedException("Access Forbidden");
            default -> new Exception("Unexpected error");
        };
    }

    private String extractResponseBody(Response response) {
        if (response.body() == null) return "No response body";

        try {
            return new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            return exception.getMessage();
        }
    }
}
