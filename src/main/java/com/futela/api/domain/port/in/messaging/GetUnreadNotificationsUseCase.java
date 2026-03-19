package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.response.messaging.NotificationResponse;

import java.util.List;
import java.util.UUID;

public interface GetUnreadNotificationsUseCase {

    List<NotificationResponse> execute(UUID userId);
}
