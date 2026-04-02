package com.doctorq.doctorservice.exception;

import com.doctorq.doctorservice.dtos.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(ResourceNotFoundException exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(exception.getMessage(), null),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotReadableException(HttpMessageNotReadableException exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(
                        exception.getMessage(), null
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ApiResponse<Object>> handleJsonProcessingException(JsonProcessingException exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(
                        exception.getMessage(), null
                ),
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleConflictException(ConflictException exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(
                        exception.getMessage(), null
                ),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Object>> handleForbiddenException(ForbiddenException exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(
                        exception.getMessage(), null
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(UnauthorizedException exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(
                        exception.getMessage(), null
                ),
                HttpStatus.UNAUTHORIZED
        );
    }
}
