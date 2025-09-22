package com.danitze.personal_finance_tracker.service.notification;

import com.danitze.personal_finance_tracker.constants.NotificationConstants;
import com.danitze.personal_finance_tracker.entity.Notification;
import com.danitze.personal_finance_tracker.entity.enums.NotificationChannel;
import com.danitze.personal_finance_tracker.entity.enums.NotificationStatus;
import com.danitze.personal_finance_tracker.exception.notification.NotificationNotFoundException;
import com.danitze.personal_finance_tracker.repository.NotificationRepository;
import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service("emailNotificationSender")
public class EmailNotificationSender implements NotificationSender {

    private final NotificationRepository notificationRepository;
    private final MailtrapClient client;

    @Autowired
    public EmailNotificationSender(
            NotificationRepository notificationRepository,
            @Value("${MAILTRAP_TOKEN}") String token
    ) {
        this.notificationRepository = notificationRepository;
        client = MailtrapClientFactory.createMailtrapClient(
                new MailtrapConfig.Builder()
                        .token(token)
                        .build()
        );
    }

    @Async
    @Override
    public void send(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);
        if (notification.getChannel() != NotificationChannel.EMAIL) {
            return;
        }
        MailtrapMail mail = MailtrapMail.builder()
                .from(new Address("financetracker@demomailtrap.co"))
                .to(List.of(new Address(notification.getAccount().getUser().getEmail())))
                .subject("Notification " + notification.getType())
                .text(notification.getMessage())
                .build();
        try {
            notification.setStatus(NotificationStatus.SENDING);
            notificationRepository.save(notification);
            client.send(mail);
            notification.setSentAt(OffsetDateTime.now());
            notification.setStatus(NotificationStatus.SENT);
            notificationRepository.save(notification);

        } catch (Exception e) {
            LoggerFactory.getLogger(EmailNotificationSender.class).atWarn().log("Failed to send notification", e);
            if (notification.getRetryCount() >= NotificationConstants.MAX_RETRY_COUNT) {
                notification.setStatus(NotificationStatus.FAILED);
                notificationRepository.save(notification);
            }
        }
    }
}
