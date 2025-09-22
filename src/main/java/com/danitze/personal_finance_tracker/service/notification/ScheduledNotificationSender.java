package com.danitze.personal_finance_tracker.service.notification;

import com.danitze.personal_finance_tracker.constants.NotificationConstants;
import com.danitze.personal_finance_tracker.entity.Notification;
import com.danitze.personal_finance_tracker.entity.enums.NotificationStatus;
import com.danitze.personal_finance_tracker.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ScheduledNotificationSender {

    private static final Logger log = LoggerFactory.getLogger(ScheduledNotificationSender.class);

    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Autowired
    public ScheduledNotificationSender(
            NotificationRepository notificationRepository,
            NotificationService notificationService
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRate = 60_000L)
    public void sendPendingNotifications() {
        OffsetDateTime now = OffsetDateTime.now();
        List<Notification> notifications = notificationRepository
                .findAllPending(
                        Set.of(NotificationStatus.NOT_SENT),
                        now
                )
                .stream()
                .filter(notification ->
                        notification.getRetryCount() <= NotificationConstants.MAX_RETRY_COUNT
                )
                .toList();
        for (Notification notification : notifications) {
            try {
                notificationService.sendNotification(notification);
            } catch (Exception e) {
                log.warn("Failed to send notification {}", notification.getId(), e);
            }
        }
    }

}
