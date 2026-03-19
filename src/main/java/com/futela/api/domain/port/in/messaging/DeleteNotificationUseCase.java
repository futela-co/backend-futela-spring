package com.futela.api.domain.port.in.messaging;

import java.util.UUID;

public interface DeleteNotificationUseCase {

    void execute(UUID notificationId, UUID userId);
}
