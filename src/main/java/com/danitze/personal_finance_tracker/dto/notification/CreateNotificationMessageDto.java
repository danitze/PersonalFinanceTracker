package com.danitze.personal_finance_tracker.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationMessageDto {

    @NonNull
    private Long accountId;

    @NonNull
    private CreateNotificationDto createNotificationDto;

}
