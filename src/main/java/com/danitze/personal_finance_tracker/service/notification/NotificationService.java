package com.danitze.personal_finance_tracker.service.notification;

import com.danitze.personal_finance_tracker.dto.notification.CreateNotificationDto;
import com.danitze.personal_finance_tracker.dto.notification.NotificationDto;
import com.danitze.personal_finance_tracker.dto.notification.NotificationMapper;
import com.danitze.personal_finance_tracker.entity.Account;
import com.danitze.personal_finance_tracker.entity.Notification;
import com.danitze.personal_finance_tracker.exception.account.AccountNotFoundException;
import com.danitze.personal_finance_tracker.exception.notification.NotificationNotFoundException;
import com.danitze.personal_finance_tracker.repository.AccountRepository;
import com.danitze.personal_finance_tracker.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    private final NotificationMapper notificationMapper;

    private final EmailNotificationSender emailNotificationSender;
    private final PushNotificationSender pushNotificationSender;

    @Autowired
    public NotificationService(
            NotificationRepository notificationRepository,
            AccountRepository accountRepository,
            NotificationMapper notificationMapper,
            EmailNotificationSender emailNotificationSender,
            PushNotificationSender pushNotificationSender
    ) {
        this.notificationRepository = notificationRepository;
        this.accountRepository = accountRepository;
        this.notificationMapper = notificationMapper;
        this.emailNotificationSender = emailNotificationSender;
        this.pushNotificationSender = pushNotificationSender;
    }

    public Notification createNotificationEntity(
            Long accountId,
            CreateNotificationDto createNotificationDto
    ) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        Notification notification = Notification.builder()
                .account(account)
                .type(createNotificationDto.getType())
                .message(createNotificationDto.getMessage())
                .data(
                        Optional.ofNullable(createNotificationDto.getData())
                                .filter(data -> !data.isBlank())
                                .orElse(null)
                )
                .triggerAt(createNotificationDto.getTriggerAt())
                .channel(createNotificationDto.getChannel())
                .build();
        return notificationRepository.save(notification);
    }

    public NotificationDto createNotification(
            Long accountId,
            CreateNotificationDto createNotificationDto
    ) {
        Notification notification = createNotificationEntity(accountId, createNotificationDto);
        return notificationMapper.toDto(notification);
    }

    public NotificationDto markRead(
            Long notificationId
    ) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);
        notification.setRead(true);
        return notificationMapper.toDto(notificationRepository.save(notification));
    }

    public Page<NotificationDto> getNotifications(
            Long accountId,
            boolean notReadOnly,
            Pageable pageable
    ) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return notificationRepository.findPageByAccount(account, notReadOnly, pageable)
                .map(notificationMapper::toDto);
    }

    public List<NotificationDto> getAllUnreadNotifications(
            Long accountId
    ) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return notificationRepository.findAllByAccountAndReadFalse(account)
                .stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public boolean isOwner(Long notificationId, String email) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);
        return notification.getAccount().getUser().getEmail().equals(email);
    }

    public void sendNotification(Notification notification) {
        switch (notification.getChannel()) {
            case EMAIL:
                emailNotificationSender.send(notification.getId());
                break;
            case PUSH:
                pushNotificationSender.send(notification.getId());
                break;
        }
    }

}
