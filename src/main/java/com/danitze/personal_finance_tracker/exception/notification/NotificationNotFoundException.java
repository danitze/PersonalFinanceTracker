package com.danitze.personal_finance_tracker.exception.notification;

public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException() {
        super("Notification not found");
    }

}
