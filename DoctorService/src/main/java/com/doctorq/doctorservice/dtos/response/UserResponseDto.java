package com.doctorq.doctorservice.dtos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public record UserResponseDto(
        Long id,
        String firstname,
        String lastname,
        String email
) {
}