package com.futela.api.domain.port.in.auth;

import java.util.UUID;

public interface ConfirmEmailUseCase {
    void execute(UUID userId, String code);
}
