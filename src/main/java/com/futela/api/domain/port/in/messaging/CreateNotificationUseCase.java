package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.response.messaging.NotificationResponse;
import com.futela.api.domain.enums.NotificationChannel;
import com.futela.api.domain.enums.NotificationType;

import java.util.Map;
import java.util.UUID;

public interface CreateNotificationUseCase {

    NotificationResponse execute(UUID userId, NotificationType type, String title, String body,
                                  NotificationChannel channel, Map<String, Object> data,
                                  UUID relatedEntityId, String relatedEntityType);
}
