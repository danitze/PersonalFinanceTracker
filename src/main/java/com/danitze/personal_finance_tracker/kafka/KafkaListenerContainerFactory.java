package com.danitze.personal_finance_tracker.kafka;

import com.danitze.personal_finance_tracker.dto.notification.CreateNotificationMessageDto;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerContainerFactory {

    @Bean("createNotificationListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, CreateNotificationMessageDto> kafkaListenerContainerFactory(
            ConsumerFactory<String, CreateNotificationMessageDto> consumerFactory,
            DefaultErrorHandler errorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, CreateNotificationMessageDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

}
