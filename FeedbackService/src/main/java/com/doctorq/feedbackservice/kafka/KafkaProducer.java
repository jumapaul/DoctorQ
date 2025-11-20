package com.doctorq.feedbackservice.kafka;

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

    public static final String TOPIC = "RATING_TOPIC";

    private final KafkaTemplate<String, FeedbackAvcEvent> kafkaTemplate;

    public CompletableFuture<SendResult<String, FeedbackAvcEvent>> publish(FeedbackAvcEvent event) {
        log.info("------------->Publishing event: {}", event);
        return kafkaTemplate.send(TOPIC, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("---------->Failed to send event={} due to {}", event, ex.getMessage(), ex);
            } else {
                log.info("--------->Event sent to topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(), result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
