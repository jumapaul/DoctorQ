package com.doctorq.appointmentservice.appointment.service;

import com.doctorq.appointmentservice.appointment.entity.CompletionOuterBoxEntity;
import com.doctorq.appointmentservice.appointment.entity.HistoryOuterBoxEntity;
import com.doctorq.appointmentservice.appointment.repository.CompletionOuterBoxRepository;
import com.doctorq.appointmentservice.appointment.repository.HistoryOuterBoxRepository;
import com.doctorq.appointmentservice.kafka.event.CompletionEvent;
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
    private final CompletionOuterBoxRepository completionOuterBoxRepository;

    @Scheduled(fixedRate = 10000)
    public void pullAndPublish() {
        List<HistoryOuterBoxEntity> unprocessedRequests = outerBoxRepository.findAll();

        unprocessedRequests.forEach(history ->
                publishHistoryEvent(history).whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Kafka ack received for event {}", history);
                        outerBoxRepository.delete(history);
                    } else {
                        log.error("Kafka send failed for event {}", history);
                    }
                })
        );
    }

    @Scheduled(fixedRate = 10000)
    public void pullAndPublishCompletionEvent() {
        List<CompletionOuterBoxEntity> unprocessedRequests = completionOuterBoxRepository.findAll();

        unprocessedRequests.forEach(complete ->
                publishCompletionEvent(complete).whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Kafka ack received for event {}", result);
                        completionOuterBoxRepository.delete(complete);
                    } else {
                        log.error("Kafka send failed for event {}", complete);
                    }
                })
        );
    }

    private CompletableFuture<SendResult<String, HistoryEvent>> publishHistoryEvent(
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

        return producer.publishHistoryEvent(event);
    }

    private CompletableFuture<SendResult<String, CompletionEvent>> publishCompletionEvent(
            CompletionOuterBoxEntity completionOuterBoxEntity
    ) {
        CompletionEvent event = new CompletionEvent(
                completionOuterBoxEntity.getId(),
                completionOuterBoxEntity.getDoctorId(),
                "Appointment complete"
        );

        return producer.publishCompletionEvent(event);
    }
}
