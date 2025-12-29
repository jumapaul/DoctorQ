package com.doctorq.doctorservice.kafka.consumer;

import com.doctorq.doctorservice.kafka.event.CompletionEvent;
import com.doctorq.doctorservice.kafka.event.FeedbackEvent;
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

    private static final String ratingConsumerGroup = "ratingGroup";

    private static final String doctorCountConsumerGroup = "COMPLETION_CONSUMER_GROUP";

    @Bean
    public ConsumerFactory<String, FeedbackEvent> feedbackConsumerFactory() {

        return new DefaultKafkaConsumerFactory<>(consumerConfigs(ratingConsumerGroup, FeedbackEvent.class));
    }

    @Bean
    public ConsumerFactory<String, CompletionEvent> patientCountConsumerFactory() {

        return new DefaultKafkaConsumerFactory<>(consumerConfigs(doctorCountConsumerGroup, CompletionEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FeedbackEvent> feedbackKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FeedbackEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(feedbackConsumerFactory());

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CompletionEvent> patientCountKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CompletionEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(patientCountConsumerFactory());

        return factory;
    }

    public Map<String, Object> consumerConfigs(String consumerGroup, Class<?> eventClass) {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, eventClass.getName());

        return props;

//        return new DefaultKafkaConsumerFactory<>(props);
    }
}
