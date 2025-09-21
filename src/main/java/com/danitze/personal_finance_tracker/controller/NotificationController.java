package com.danitze.personal_finance_tracker.controller;

import com.danitze.personal_finance_tracker.dto.notification.NotificationDto;
import com.danitze.personal_finance_tracker.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PutMapping("/{id}/mark-read")
    @PreAuthorize("hasRole('ADMIN') or @notificationService.isOwner(#id, principal.username)")
    public ResponseEntity<NotificationDto> markRead(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(notificationService.markRead(id));
    }

}
