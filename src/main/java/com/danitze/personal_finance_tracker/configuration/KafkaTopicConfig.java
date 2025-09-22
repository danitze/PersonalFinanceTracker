package com.danitze.personal_finance_tracker.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic notificationsTopic(
            @Value("${kafka.topic.notifications}") String notificationsTopic
    ) {
        return new NewTopic(notificationsTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic notificationsDlq() {
        return new NewTopic("notifications.DLQ", 1, (short) 1);
    }

}
