package com.doctorq.userservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, UserToDoctorRequestEvent> kafkaTemplate;
    public static final String TOPIC = "AssignDoctorTopic";

    public CompletableFuture<SendResult<String, UserToDoctorRequestEvent>> publish(UserToDoctorRequestEvent event) {
        return kafkaTemplate.send(TOPIC, event).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("------>Failed to send event={} due to {}", event, ex.getMessage());
            } else {
                log.info("------->Event sent to topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(), result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
            }
        });
    }
}
