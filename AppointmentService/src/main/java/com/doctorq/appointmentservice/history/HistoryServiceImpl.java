package com.doctorq.appointmentservice.history;

import com.doctorq.appointmentservice.appointment.dtos.PaginatedResponse;
import com.doctorq.appointmentservice.util.RedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.doctorq.appointmentservice.util.Constants.allHistoryCache;
import static com.doctorq.appointmentservice.util.RedisRetrieveMethods.readCacheValue;
import static com.doctorq.appointmentservice.util.RedisRetrieveMethods.setCacheValue;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final HistoryRepository historyRepository;
    private final RedisUtil redisUtil;

    @Override
    public PaginatedResponse<HistoryEntity> getAllUserHistory(Long userId, int page, int size) throws JsonProcessingException {
        Object historyCache = redisUtil.get(allHistoryCache + page + size);

        if (historyCache == null) {
            Pageable pageable = PageRequest.of(page, size);

            Page<HistoryEntity> history = historyRepository.findAllByUserId(pageable, userId);

            List<HistoryEntity> historyEntityList = history.stream().toList();
            PaginatedResponse<HistoryEntity> paginatedResponse = paginate(historyEntityList, history);
            setCacheValue(redisUtil, allHistoryCache + page + size, paginatedResponse);
            return paginatedResponse;
        }

        return readCacheValue(historyCache.toString(), new TypeReference<>() {
        });
    }

    private <T> PaginatedResponse<T> paginate(List<T> data, Page<?> paginatedData) {
        return new PaginatedResponse<>(
                data,
                paginatedData.getNumber(),
                paginatedData.getTotalPages(),
                paginatedData.getSize(),
                paginatedData.getNumberOfElements(),
                paginatedData.getSort().isSorted(),
                paginatedData.isLast()
        );
    }
}
