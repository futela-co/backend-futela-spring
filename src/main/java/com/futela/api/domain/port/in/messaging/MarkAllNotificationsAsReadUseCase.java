package com.futela.api.domain.port.in.messaging;

import java.util.UUID;

public interface MarkAllNotificationsAsReadUseCase {

    void execute(UUID userId);
}
