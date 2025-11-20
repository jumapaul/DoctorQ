package com.doctorq.favoriteservice.dtos;

public record FavoriteDoctorRequest(
        Long userId,
        Long doctorId
) {
}
