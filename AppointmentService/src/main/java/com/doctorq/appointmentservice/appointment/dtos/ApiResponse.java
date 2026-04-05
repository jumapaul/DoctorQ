package com.doctorq.appointmentservice.appointment.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private String message;
    private T data;
    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
