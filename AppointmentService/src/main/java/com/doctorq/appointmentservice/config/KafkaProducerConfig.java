package com.doctorq.appointmentservice.config;

import com.doctorq.appointmentservice.kafka.AppointmentCompletionEvent;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${security.protocol}")
    private String securityProtocol;

    @Value("${sasl.mechanism}")
    private String sslMechanism;

    @Value("${session.timeout.ms}")
    private String sessionTimeout;

    @Value("${client.dns.lookup}")
    private String clientDns;

    @Value("${sasl.jaas.config}")
    private String jaasConfig;
    @Bean
    public ProducerFactory<String, AppointmentCompletionEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put("security.protocol", securityProtocol);
        configProps.put("sasl.mechanism", sslMechanism);
        configProps.put("sasl.jaas.config", jaasConfig);

        configProps.put("client.dns.lookup", clientDns);
        configProps.put("session.timeout.ms", sessionTimeout);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, AppointmentCompletionEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
