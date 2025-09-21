package com.danitze.personal_finance_tracker.dto.notification;

import com.danitze.personal_finance_tracker.entity.enums.NotificationChannel;
import com.danitze.personal_finance_tracker.entity.enums.NotificationType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNotificationDto {

    @NotNull
    private NotificationType type;

    @NotNull
    private String message;

    @Nullable
    private String data;

    @Nullable
    private OffsetDateTime triggerAt;

    @NotNull
    private NotificationChannel channel;

    @AssertTrue(message = "Trigger at should be later than now")
    public boolean checkTriggerAt() {
        if (triggerAt == null) {
            return true;
        }
        return triggerAt.isAfter(OffsetDateTime.now());
    }

}
