package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.response.messaging.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetUserNotificationsUseCase {

    Page<NotificationResponse> execute(UUID userId, Pageable pageable);
}
