package com.futela.api.domain.event;

import com.futela.api.domain.enums.NotificationChannel;
import com.futela.api.domain.enums.NotificationType;

import java.util.UUID;

public record NotificationCreatedEvent(
        UUID notificationId,
        UUID userId,
        NotificationType type,
        NotificationChannel channel,
        String title,
        String body
) {
}
