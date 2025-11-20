package com.doctorq.favoriteservice.controller;

import com.doctorq.favoriteservice.dtos.FavoriteDoctorRequest;
import com.doctorq.favoriteservice.response.ApiResponse;
import com.doctorq.favoriteservice.response.DoctorResponse;
import com.doctorq.favoriteservice.response.PaginatedResponse;
import com.doctorq.favoriteservice.service.FavoriteDoctorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteDoctorService favoriteDoctorService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<DoctorResponse>> addToFavorite(
            @RequestBody FavoriteDoctorRequest request
    ) {
        DoctorResponse response = favoriteDoctorService.addDoctor(request);
        return ResponseEntity.ok(response(response, "Added to favorite successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFromFavoritesById(
            @PathVariable(name = "id") Long id
    ) {
        favoriteDoctorService.deleteFromFavoriteById(id);

        return ResponseEntity.ok(response(null, "Doctor removed from favorite"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<PaginatedResponse<DoctorResponse>> getAllFavorites(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws JsonProcessingException {
        PaginatedResponse<DoctorResponse> response = favoriteDoctorService.getAllUserFavoriteDoctors(userId, page, size);

        return ResponseEntity.ok(response);
    }

    private <T> ApiResponse<T> response(T data, String message) {
        return new ApiResponse<T>(
                HttpStatus.OK.value(),
                message,
                data
        );
    }
}
