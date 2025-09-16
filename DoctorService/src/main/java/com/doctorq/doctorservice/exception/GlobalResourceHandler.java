package com.doctorq.doctorservice.exception;

import com.doctorq.doctorservice.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalResourceHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(ResourceNotFoundException exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(HttpStatus.NOT_FOUND.value(), exception.getMessage(), null),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotReadableException(HttpMessageNotReadableException exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(
                        HttpStatus.BAD_REQUEST.value(),
                        exception.getMessage(), null
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Object>> handleConflictException(ConflictException exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(
                        HttpStatus.CONFLICT.value(),
                        exception.getMessage(), null
                ),
                HttpStatus.CONFLICT
        );
    }
}
