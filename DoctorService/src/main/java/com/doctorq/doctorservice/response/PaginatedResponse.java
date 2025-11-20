package com.doctorq.doctorservice.response;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> data,
        int pageNumber,
        int totalPages,
        int pageSize,
        int totalElements,
        boolean isSorted,
        boolean isLastPage
) {
}
