package com.doctorq.doctorservice.kafka.consumer;

import com.doctorq.doctorservice.kafka.event.AppointmentCompletionEvent;
import com.doctorq.doctorservice.kafka.event.UserToDoctorRequestEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class AssignDoctorConsumerConfig {
    private String bootstrapServer = "pkc-921jm.us-east-2.aws.confluent.cloud:9092";

    private static final String assignDoctorConsumerGroup = "ASSIGN_DOCTOR_CONSUMER_GROUP";

    @Bean
    public ConsumerFactory<String, UserToDoctorRequestEvent> assignDoctorConsumerFactory() {

        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, assignDoctorConsumerGroup);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        JsonDeserializer<UserToDoctorRequestEvent> deserializer =
                new JsonDeserializer<>(UserToDoctorRequestEvent.class);

        deserializer.addTrustedPackages("*");
        deserializer.ignoreTypeHeaders();
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "PLAIN");
        props.put("sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule required username='OVBDOZSLI5NB57JW' password='cfltoq42UfI0G86K8hgvSDiQ3qJrrkSNPvrUwOWG0pyal4USpjvJiKS3eqR/n3wA';");

        props.put("client.dns.lookup", "use_all_dns_ips");
        props.put("session.timeout.ms", 45000);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserToDoctorRequestEvent> assignDoctorListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserToDoctorRequestEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(assignDoctorConsumerFactory());

        return factory;
    }
}
