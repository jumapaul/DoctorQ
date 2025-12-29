package com.doctorq.appointmentservice.kafka.producer;

import com.doctorq.appointmentservice.kafka.event.CompletionEvent;
import com.doctorq.appointmentservice.kafka.event.HistoryEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProducerConfigurations {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean(name = "historyKafkaTemplate")
    public KafkaTemplate<String, HistoryEvent> historyKafkaTemplate(
            @Qualifier("historyProducerFactory") ProducerFactory<String, HistoryEvent> producerFactory
    ) {

        return new KafkaTemplate<>(producerFactory);
    }

    @Bean(name = "completionKafkaTemplate")
    public KafkaTemplate<String, CompletionEvent> completionKafkaTemplate(
            @Qualifier("completionProducerFactory") ProducerFactory<String, CompletionEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean(name = "historyProducerFactory")
    public ProducerFactory<String, HistoryEvent> historyProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean(name = "completionProducerFactory")
    public ProducerFactory<String, CompletionEvent> completionProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ENABLE_METRICS_PUSH_CONFIG, false);
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 60000);
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 100);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        return configProps;
    }
}
