package com.doctorq.appointmentservice.exception;

import org.springframework.http.HttpStatus;

public class FirebaseMessaginException extends RuntimeException {
    private final HttpStatus status;

    public FirebaseMessaginException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}