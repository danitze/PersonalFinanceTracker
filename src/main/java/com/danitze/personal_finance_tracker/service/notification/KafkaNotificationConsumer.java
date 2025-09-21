package com.danitze.personal_finance_tracker.service.notification;

import com.danitze.personal_finance_tracker.dto.notification.CreateNotificationMessageDto;
import com.danitze.personal_finance_tracker.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaNotificationConsumer {

    private final NotificationService notificationService;

    @Autowired
    public KafkaNotificationConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${kafka.topic.notifications}", groupId = "notifications-service")
    public void listen(CreateNotificationMessageDto createNotificationMessageDto) {
        Notification notification = notificationService.createNotificationEntity(
                createNotificationMessageDto.getAccountId(),
                createNotificationMessageDto.getCreateNotificationDto()
        );
        notificationService.sendNotification(notification);
    }

}
