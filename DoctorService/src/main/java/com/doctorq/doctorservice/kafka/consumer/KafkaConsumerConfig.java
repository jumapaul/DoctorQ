package com.doctorq.doctorservice.kafka.consumer;

import com.doctorq.doctorservice.kafka.event.AppointmentCompletionEvent;
import com.doctorq.doctorservice.kafka.event.UserToDoctorRequestEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    @Value("${sasl.jaas.config}")
    private String jaasConfig;

    @Value("${security.protocol}")
    private String securityProtocol;

    @Value("${client.dns.lookup}")
    private String dnsLookUp;

    @Value("${session.timeout.ms}")
    private String sessionTimeOut;

    @Value("${sasl.mechanism}")
    private String saslMechanism;

    private static final String completionConsumerGroup = "APPOINTMENT_COMPLETION_GROUP";
    private static final String assignDoctorConsumerGroup = "AssignDoctorTopic";


    public Map<String, Object> baseConsumerProps(String groupId) {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        props.put("security.protocol", securityProtocol);
        props.put("sasl.mechanism", saslMechanism);
        props.put("sasl.jaas.config", jaasConfig);

        props.put("client.dns.lookup", dnsLookUp);
        props.put("session.timeout.ms", sessionTimeOut);

        return props;
    }

    //Completion
    @Bean
    public ConsumerFactory<String, AppointmentCompletionEvent> completionConsumerFactory() {

        JsonDeserializer<AppointmentCompletionEvent> deserializer =
                new JsonDeserializer<>(AppointmentCompletionEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.ignoreTypeHeaders();
        return new DefaultKafkaConsumerFactory<>(
                baseConsumerProps(completionConsumerGroup),
                new StringDeserializer(), deserializer);
    }

    @Bean(name = "completionListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AppointmentCompletionEvent>
    completionKafkaListenerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, AppointmentCompletionEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(completionConsumerFactory());
        factory.setConcurrency(3);
        factory.setCommonErrorHandler(new DefaultErrorHandler());

        return factory;
    }

    //Assign doctor
    @Bean
    public ConsumerFactory<String, UserToDoctorRequestEvent> assignDoctorConsumerFactory() {
        JsonDeserializer<UserToDoctorRequestEvent> deserializer =
                new JsonDeserializer<>(UserToDoctorRequestEvent.class);

        deserializer.addTrustedPackages("*");
        deserializer.ignoreTypeHeaders();

        return new DefaultKafkaConsumerFactory<>(
                baseConsumerProps(assignDoctorConsumerGroup),
                new StringDeserializer(), deserializer
        );
    }

    @Bean(name = "assignDoctorListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, UserToDoctorRequestEvent>
    assignDoctorKafkaListenerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, UserToDoctorRequestEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(assignDoctorConsumerFactory());

        factory.setConcurrency(3);
        factory.setCommonErrorHandler(new DefaultErrorHandler());

        return factory;
    }
}