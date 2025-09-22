package com.danitze.personal_finance_tracker.controller;

import com.danitze.personal_finance_tracker.dto.notification.NotificationDto;
import com.danitze.personal_finance_tracker.service.notification.NotificationDLQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dlq/notifications")
public class NotificationDLQController {

    private final NotificationDLQService notificationDLQService;

    @Autowired
    public NotificationDLQController(NotificationDLQService notificationDLQService) {
        this.notificationDLQService = notificationDLQService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getFailedNotifications() {
        return ResponseEntity.ok(notificationDLQService.getFailedNotifications());
    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<Void> retryFailedNotification(
            @PathVariable Long id
    ) {
        notificationDLQService.retryFailedNotification(id);
        return ResponseEntity.accepted().build();
    }

}
