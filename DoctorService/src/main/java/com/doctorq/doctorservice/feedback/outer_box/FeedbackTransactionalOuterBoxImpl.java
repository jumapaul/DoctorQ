package com.doctorq.doctorservice.feedback.outer_box;

import com.doctorq.doctorservice.kafka.event.FeedbackEvent;
import com.doctorq.doctorservice.kafka.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class FeedbackTransactionalOuterBoxImpl {

    private final KafkaProducer producer;
    private final FeedOuterBoxRepository outerBoxRepository;

    @Scheduled(fixedRate = 10000)
    public void pullAndPublish() {
        List<FeedbackOuterBoxEntity> unprocessedRequests = outerBoxRepository.findAll();

        unprocessedRequests.forEach(feedbackOuterBoxEntity -> {
            publishFeedback(feedbackOuterBoxEntity).whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Kafka ack received for event {}", feedbackOuterBoxEntity);
                    outerBoxRepository.delete(feedbackOuterBoxEntity);
                } else {
                    log.error("Kafka send failed for event {}", feedbackOuterBoxEntity);
                }
            });
        });
    }

    private CompletableFuture<SendResult<String, FeedbackEvent>> publishFeedback(
            FeedbackOuterBoxEntity feedbackOuterBoxEntity
    ) {
        FeedbackEvent event = new FeedbackEvent(
                feedbackOuterBoxEntity.getId(),
                feedbackOuterBoxEntity.getDoctorId(),
                feedbackOuterBoxEntity.getRating(),
                feedbackOuterBoxEntity.getRatingCount(),
                feedbackOuterBoxEntity.getReviewsCount()
        );

        return producer.publish(event);
    }
}
