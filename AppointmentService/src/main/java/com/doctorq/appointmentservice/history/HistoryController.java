package com.doctorq.appointmentservice.history;

import com.doctorq.appointmentservice.appointment.dtos.PaginatedResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/history")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping("/{userId}")
    public ResponseEntity<PaginatedResponse<HistoryEntity>> getUserHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page
    ) throws JsonProcessingException {
        return ResponseEntity.ok(historyService.getAllUserHistory(userId, page, size));
    }
}
