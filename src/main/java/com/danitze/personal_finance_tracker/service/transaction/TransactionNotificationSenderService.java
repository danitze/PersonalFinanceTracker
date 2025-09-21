package com.danitze.personal_finance_tracker.service.transaction;

import com.danitze.personal_finance_tracker.dto.notification.CreateNotificationDto;
import com.danitze.personal_finance_tracker.dto.notification.CreateNotificationMessageDto;
import com.danitze.personal_finance_tracker.kafka.KafkaNotificationProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TransactionNotificationSenderService {

    private final Logger logger = LoggerFactory.getLogger(TransactionNotificationSenderService.class);

    private final KafkaNotificationProducer notificationProducer;
    private final String notificationsTopic;

    public TransactionNotificationSenderService(
            KafkaNotificationProducer notificationProducer,
            @Value("${kafka.topic.notifications}") String notificationsTopic
    ) {
        this.notificationProducer = notificationProducer;
        this.notificationsTopic = notificationsTopic;
    }

    public void sendTransactionNotification(Long accountId, CreateNotificationDto createNotificationDto) {
        notificationProducer.sendNotification(
                notificationsTopic,
                new CreateNotificationMessageDto(
                        accountId,
                        createNotificationDto
                )
        );
        logger.info("Notification sent to account {}", accountId);
    }

}
