package com.doctorq.appointmentservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopic {

    @Bean
    public NewTopic completionTopic() {
        return TopicBuilder.name("COMPLETION_TOPIC")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
