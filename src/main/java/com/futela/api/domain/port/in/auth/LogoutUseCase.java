package com.futela.api.domain.port.in.auth;

import java.util.UUID;

public interface LogoutUseCase {
    void execute(UUID sessionId);
}
