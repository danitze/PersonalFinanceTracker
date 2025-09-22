package com.danitze.personal_finance_tracker.service.notification;

import com.danitze.personal_finance_tracker.entity.Notification;
import com.danitze.personal_finance_tracker.entity.enums.NotificationChannel;
import com.danitze.personal_finance_tracker.exception.notification.NotificationNotFoundException;
import com.danitze.personal_finance_tracker.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service("pushNotificationSender")
public class PushNotificationSender implements NotificationSender {

    private final NotificationRepository notificationRepository;

    public PushNotificationSender(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Async
    @Override
    public void send(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);
        if (notification.getChannel() != NotificationChannel.PUSH) {
            return;
        }
        System.out.println("Sending push to " + notification.getAccount().getUser().getId() +
                ": " + notification.getMessage());
        notification.setSentAt(OffsetDateTime.now());
        notificationRepository.save(notification);
    }
}
