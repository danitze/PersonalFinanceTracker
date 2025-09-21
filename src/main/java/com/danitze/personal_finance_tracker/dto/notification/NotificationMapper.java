package com.danitze.personal_finance_tracker.dto.notification;

import com.danitze.personal_finance_tracker.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationDto toDto(Notification notification);
}
