package com.doctorq.userservice.exception;

import com.doctorq.userservice.response.ApiResponse;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.io.FileNotFoundException;
import java.util.ArrayList;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflictException(ConflictException exception) {
        return new ResponseEntity<>(new ApiResponse<>(
                HttpStatus.CONFLICT.value(), exception.getMessage(), null
        ), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(ResourceNotFoundException exception) {
        return new ResponseEntity<>(new ApiResponse<>(
                HttpStatus.NOT_FOUND.value(), exception.getMessage(), null
        ), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception) {
        return new ResponseEntity<>(new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(), exception.getMessage(), null
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbiddenException(ForbiddenException exception) {
        return new ResponseEntity<>(new ApiResponse<>(
                HttpStatus.FORBIDDEN.value(), exception.getMessage(), null
        ), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException exception) {
        return new ResponseEntity<>(new ApiResponse<>(
                HttpStatus.BAD_REQUEST.value(), exception.getMessage(), null
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
                HttpStatus.BAD_REQUEST.value(), errors.isEmpty() ? "Validation Failed" : errors.get(0), null
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<Object> handleMessagingException(MessagingException exception) {
        return new ResponseEntity<>(new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage(), null
        ), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception) {
        return new ResponseEntity<>(new ApiResponse<>(
                HttpStatus.FORBIDDEN.value(), exception.getMessage(), null
        ), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Object> handleFileNotFoundException(FileNotFoundException exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(
                        HttpStatus.NOT_FOUND.value(),
                        exception.getMessage(),
                        null
                ), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<Object> handleInternalServerErrorException(InternalServerErrorException exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        exception.getMessage(),
                        null
                ), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Object> handleServiceUnavailable(ServiceUnavailableException exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        exception.getMessage(),
                        null
                ), HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<Object> handleUnAuthorizedException(UnAuthorizedException exception) {
        return new ResponseEntity<>(
                new ApiResponse<>(
                        HttpStatus.UNAUTHORIZED.value(),
                        exception.getMessage(),
                        null
                ),
                HttpStatus.UNAUTHORIZED
        );
    }
}
