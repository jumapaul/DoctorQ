package com.doctorq.userservice.favorite.dtos;

public record FavoriteDoctorRequest(
        Long userId,
        Long doctorId
) {
}
