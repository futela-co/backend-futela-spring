package com.futela.api.domain.port.in.messaging;

import com.futela.api.application.dto.response.messaging.UnreadCountResponse;

import java.util.UUID;

public interface GetUnreadNotificationsCountUseCase {

    UnreadCountResponse execute(UUID userId);
}
