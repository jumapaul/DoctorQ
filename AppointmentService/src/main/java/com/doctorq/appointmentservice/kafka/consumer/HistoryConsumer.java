package com.doctorq.appointmentservice.kafka.consumer;

import com.doctorq.appointmentservice.history.HistoryEntity;
import com.doctorq.appointmentservice.history.HistoryRepository;
import com.doctorq.appointmentservice.kafka.event.HistoryEvent;
import com.doctorq.appointmentservice.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.doctorq.appointmentservice.util.Constants.allHistoryCache;

@Slf4j
@Component
@RequiredArgsConstructor
public class HistoryConsumer {

    private final HistoryRepository historyRepository;
    private static final String HistoryTopic = "HISTORY_TOPIC";
    private static final String HistoryGroup = "HISTORY_GROUP";
    private final RedisUtil redisUtil;

    @KafkaListener(topics = HistoryTopic, groupId = HistoryGroup)
    public void listen(HistoryEvent event) {
        HistoryEntity entity = HistoryEntity.builder()
                .userId(event.getUserId())
                .doctorId(event.getDoctorId())
                .activityName(event.getActivityName())
                .historyStatus(event.getHistoryStatus())
                .timestamp(event.getTimestamp())
                .build();
        historyRepository.save(entity);
        redisUtil.deleteByPrefix(allHistoryCache);
    }
}
