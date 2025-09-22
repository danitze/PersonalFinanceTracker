package com.danitze.personal_finance_tracker.service.notification;

import com.danitze.personal_finance_tracker.constants.NotificationConstants;
import com.danitze.personal_finance_tracker.dto.notification.NotificationDto;
import com.danitze.personal_finance_tracker.dto.notification.NotificationMapper;
import com.danitze.personal_finance_tracker.entity.Notification;
import com.danitze.personal_finance_tracker.entity.enums.NotificationStatus;
import com.danitze.personal_finance_tracker.exception.notification.NotificationNotFoundException;
import com.danitze.personal_finance_tracker.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationDLQService {

    private final Logger logger = LoggerFactory.getLogger(NotificationDLQService.class);

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationDLQService(
            NotificationRepository notificationRepository,
            NotificationMapper notificationMapper
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;

    }

    public List<NotificationDto> getFailedNotifications() {
        return notificationRepository.findAllByStatus(NotificationStatus.FAILED)
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    public void retryFailedNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);
        if (notification.getRetryCount() > NotificationConstants.MAX_RETRY_COUNT) {
            logger.warn("Notification {} retry count exceeded", notificationId);
            return;
        }
        notification.setStatus(NotificationStatus.NOT_SENT);
        notification.setRetryCount(notification.getRetryCount() + 1);
        notificationRepository.save(notification);
    }

}
