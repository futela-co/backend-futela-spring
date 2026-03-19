package com.futela.api.domain.port.in.auth;

import java.util.UUID;

public interface SendPhoneCodeUseCase {
    void execute(UUID userId);
}
