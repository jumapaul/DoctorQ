package com.doctorq.doctorservice.favorite;

import com.doctorq.doctorservice.dtos.response.ApiResponse;
import com.doctorq.doctorservice.dtos.response.DoctorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/add")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<String>> addToFavorite(
            @RequestBody FavoriteRequest request
    ) {
        String message = favoriteService.addToFavorite(request);
        return ResponseEntity.ok(response(null, message));
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorResponse>> removeFromFavorite(
            @RequestBody FavoriteRequest request
    ) {
        String message = favoriteService.removeFromFavorite(request);
        return ResponseEntity.ok(response(null, message));
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> removeFromFavorite(
            @PathVariable Long id
    ) throws JsonProcessingException {
        List<DoctorResponse> doctors = favoriteService.getUserFavorites(id);
        return ResponseEntity.ok(response(doctors, "User favorite retrieved"));
    }

    private <T> ApiResponse<T> response(T data, String message) {
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                message,
                data
        );
    }
}
