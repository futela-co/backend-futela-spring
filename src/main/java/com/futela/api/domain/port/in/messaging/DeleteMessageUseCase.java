package com.futela.api.domain.port.in.messaging;

import java.util.UUID;

public interface DeleteMessageUseCase {

    void execute(UUID messageId, UUID userId);
}
