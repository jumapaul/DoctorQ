package com.doctorq.doctorservice.favorite;

public record FavoriteRequest(
        Long userId,
        Long doctorId
) {
}
