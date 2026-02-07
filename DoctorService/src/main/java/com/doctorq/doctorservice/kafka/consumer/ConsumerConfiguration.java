package com.doctorq.doctorservice.kafka.consumer;

import com.doctorq.doctorservice.kafka.event.AppointmentCompletionEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class ConsumerConfiguration {
    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServer;

    private static final String completionConsumerGroup = "APPOINTMENT_COMPLETION_GROUP";

    @Bean
    public ConsumerFactory<String, AppointmentCompletionEvent> completionConsumerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, completionConsumerGroup);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        JsonDeserializer<AppointmentCompletionEvent> deserializer =
                new JsonDeserializer<>(AppointmentCompletionEvent.class);

        deserializer.addTrustedPackages("*");
        deserializer.ignoreTypeHeaders();
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AppointmentCompletionEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AppointmentCompletionEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(completionConsumerFactory());

        return factory;
    }
}
