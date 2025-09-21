package com.danitze.personal_finance_tracker.dto.notification;

import com.danitze.personal_finance_tracker.entity.enums.NotificationChannel;
import com.danitze.personal_finance_tracker.entity.enums.NotificationStatus;
import com.danitze.personal_finance_tracker.entity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    @NonNull
    private Long id;

    @NonNull
    private NotificationType type;

    @NonNull
    private String message;

    @Nullable
    private String data;

    @Nullable
    private OffsetDateTime triggerAt;

    @NonNull
    private OffsetDateTime createdAt;

    @Nullable
    private OffsetDateTime sentAt;

    @NonNull
    private Boolean read;

    @NonNull
    private NotificationChannel channel;

    @NonNull
    private NotificationStatus status;

}
