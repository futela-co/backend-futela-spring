package com.futela.api.domain.port.in.auth;

import java.util.UUID;

public interface LogoutDeviceUseCase {
    void execute(UUID sessionId);
}
