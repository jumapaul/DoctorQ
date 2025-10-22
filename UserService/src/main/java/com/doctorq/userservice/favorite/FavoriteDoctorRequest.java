package com.doctorq.userservice.favorite;

public record FavoriteDoctorRequest(
        Long userId,
        Long doctorId
) {
}
