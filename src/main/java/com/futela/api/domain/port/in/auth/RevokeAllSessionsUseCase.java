package com.futela.api.domain.port.in.auth;

import java.util.UUID;

public interface RevokeAllSessionsUseCase {
    int execute(UUID userId);
}
