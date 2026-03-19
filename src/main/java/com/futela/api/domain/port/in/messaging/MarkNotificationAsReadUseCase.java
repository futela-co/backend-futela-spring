package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.response.messaging.NotificationResponse;

import java.util.UUID;

public interface MarkNotificationAsReadUseCase {

    NotificationResponse execute(UUID notificationId, UUID userId);
}
