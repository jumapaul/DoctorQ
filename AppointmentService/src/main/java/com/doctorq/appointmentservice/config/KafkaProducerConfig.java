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

//    security.protocol=SASL_SSL
//    sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username='OVBDOZSLI5NB57JW' password='cfltoq42UfI0G86K8hgvSDiQ3qJrrkSNPvrUwOWG0pyal4USpjvJiKS3eqR/n3wA';
//    sasl.mechanism=PLAIN
//# Required for correctness in Apache Kafka clients prior to 2.6
//    client.dns.lookup=use_all_dns_ips
//
//# Best practice for higher availability in Apache Kafka clients prior to 3.0
//    session.timeout.ms=45000
//
//            # Best practice for Kafka producer to prevent data loss
//    acks=all
//
//    client.id=ccloud-java-client-7a1b88b7-28a4-4c5b-9edb-ee3a3d62dd1b

    private String bootstrapAddress = "pkc-921jm.us-east-2.aws.confluent.cloud:9092";

    @Bean
    public ProducerFactory<String, AppointmentCompletionEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put("security.protocol", "SASL_SSL");
        configProps.put("sasl.mechanism", "PLAIN");
        configProps.put("sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule required username='OVBDOZSLI5NB57JW' password='cfltoq42UfI0G86K8hgvSDiQ3qJrrkSNPvrUwOWG0pyal4USpjvJiKS3eqR/n3wA';");

        configProps.put("client.dns.lookup", "use_all_dns_ips");
        configProps.put("session.timeout.ms", 45000);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, AppointmentCompletionEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
