package com.danitze.personal_finance_tracker.configuration;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(
            KafkaTemplate<Object, Object> kafkaTemplate
    ) {
        DeadLetterPublishingRecoverer recoverer =  new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) ->
                        new TopicPartition(record.topic() + ".DLQ", record.partition())
        );
        return new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(1000L, 3L)
        );
    }

}
