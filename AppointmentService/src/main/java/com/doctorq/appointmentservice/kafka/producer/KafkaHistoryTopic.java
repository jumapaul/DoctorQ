package com.doctorq.appointmentservice.kafka.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaHistoryTopic {

    @Bean
    public NewTopic historyTopic() {

        return TopicBuilder
                .name("HISTORY_TOPIC")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic completionTopic() {

        return TopicBuilder
                .name("COMPLETE_TOPIC")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
