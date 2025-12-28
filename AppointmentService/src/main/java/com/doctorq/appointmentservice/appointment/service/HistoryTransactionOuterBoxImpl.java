package com.doctorq.appointmentservice.appointment.service;

import com.doctorq.appointmentservice.appointment.entity.HistoryOuterBoxEntity;
import com.doctorq.appointmentservice.appointment.repository.HistoryOuterBoxRepository;
import com.doctorq.appointmentservice.kafka.event.HistoryEvent;
import com.doctorq.appointmentservice.kafka.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class HistoryTransactionOuterBoxImpl {

    private final KafkaProducer producer;
    private final HistoryOuterBoxRepository outerBoxRepository;

    @Scheduled(fixedRate = 10000)
    public void pullAndPublish() {
        List<HistoryOuterBoxEntity> unprocessedRequests = outerBoxRepository.findAll();

        unprocessedRequests.forEach(history ->
                publishFeedback(history).whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Kafka ack received for event {}", history);
                        outerBoxRepository.delete(history);
                    } else {
                        log.error("Kafka send failed for event {}", history);
                    }
                })
        );
    }

    private CompletableFuture<SendResult<String, HistoryEvent>> publishFeedback(
            HistoryOuterBoxEntity historyOuterBoxEntity
    ) {
        HistoryEvent event = new HistoryEvent(
                historyOuterBoxEntity.getId(),
                historyOuterBoxEntity.getUserId(),
                historyOuterBoxEntity.getDoctorId(),
                historyOuterBoxEntity.getActivityName(),
                historyOuterBoxEntity.getHistoryStatus(),
                historyOuterBoxEntity.getTimestamp(),
                historyOuterBoxEntity.getProcessedStatus()
        );

        return producer.publish(event);
    }
}
