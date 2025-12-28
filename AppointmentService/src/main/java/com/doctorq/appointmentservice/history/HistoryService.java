package com.doctorq.appointmentservice.history;

import com.doctorq.appointmentservice.appointment.dtos.PaginatedResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface HistoryService {

    PaginatedResponse<HistoryEntity> getAllUserHistory(Long userId, int page, int size) throws JsonProcessingException;
}
