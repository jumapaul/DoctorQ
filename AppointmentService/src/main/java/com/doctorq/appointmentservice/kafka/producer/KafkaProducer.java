package com.doctorq.appointmentservice.kafka.producer;

import com.doctorq.appointmentservice.kafka.event.CompletionEvent;
import com.doctorq.appointmentservice.kafka.event.HistoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {
    public static final String TOPIC = "HISTORY_TOPIC";

    public static final String PATIENT_COUNT_TOPIC = "COMPLETE_TOPIC";

    private final KafkaTemplate<String, HistoryEvent> kafkaHistoryTemplate;
    private final KafkaTemplate<String, CompletionEvent> kafkaCompletionTemplate;

    public CompletableFuture<SendResult<String, HistoryEvent>> publishHistoryEvent(HistoryEvent event) {
        log.info("-------------Publishing history event: {}", event);

        return kafkaHistoryTemplate.send(TOPIC, event).whenComplete(this::logResults);
    }

    public CompletableFuture<SendResult<String, CompletionEvent>> publishCompletionEvent(CompletionEvent event) {
        log.info("-------------Publishing completion event: {}", event);
        return kafkaCompletionTemplate.send(PATIENT_COUNT_TOPIC, event).whenComplete(this::logResults);
    }

    private <T> void logResults(SendResult<String, T> result, Throwable ex) {
        if (ex != null) {
            log.error("Failed to send event due to: {}", ex.getMessage());
        } else {
            log.info("--------->Event sent to topic={}, partition={}, offset={}",
                    result.getRecordMetadata().topic(), result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
        }
    }
}
