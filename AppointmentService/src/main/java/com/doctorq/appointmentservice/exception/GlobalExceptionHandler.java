package com.doctorq.appointmentservice.exception;

import com.doctorq.appointmentservice.appointment.dtos.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;

/**
 * Exception handler
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(ResourceNotFoundException exception) {
        return new ResponseEntity<>(new ApiResponse<>(
                exception.getMessage(), null
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception) {
        return new ResponseEntity<>(new ApiResponse<>(
                exception.getMessage(), null
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleInvalidArguments(MethodArgumentNotValidException exception) {
        var errors = new ArrayList<String>();

        exception.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });
        return new ResponseEntity<>(new ApiResponse<>(
                errors.isEmpty() ? "Validation Failed" : errors.get(0), null
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<Object> handleMessagingException(MessagingException exception) {
        return new ResponseEntity<>(new ApiResponse<>(
                exception.getMessage(), null
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException exception) {
        return new ResponseEntity<>(new ApiResponse<>(
                exception.getMessage(), null
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception) {
        return new ResponseEntity<>(new ApiResponse<>(
                exception.getMessage(), null
        ), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Object> handleJsonProcessingException(JsonProcessingException exception) {
        return new ResponseEntity<>(new ApiResponse<>(
                exception.getMessage(), null
        ), HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(FirebaseMessaginException.class)
    public ResponseEntity<ApiResponse<Object>> handleFirebaseMessagingException(FirebaseMessaginException exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(
                        exception.getMessage(), null
                ),
                exception.getStatus()
        );
    }
}
