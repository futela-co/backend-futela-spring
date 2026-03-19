package com.futela.api.domain.model.messaging;

import com.futela.api.domain.enums.NotificationChannel;
import com.futela.api.domain.enums.NotificationStatus;
import com.futela.api.domain.enums.NotificationType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record Notification(
        UUID id,
        UUID userId,
        NotificationType type,
        NotificationStatus status,
        String title,
        String body,
        NotificationChannel channel,
        Map<String, Object> data,
        UUID relatedEntityId,
        String relatedEntityType,
        boolean isRead,
        Instant readAt,
        UUID companyId,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt
) {
}
