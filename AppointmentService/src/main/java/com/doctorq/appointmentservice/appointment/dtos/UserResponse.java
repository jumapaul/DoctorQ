package com.doctorq.appointmentservice.appointment.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class UserResponse {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
}
