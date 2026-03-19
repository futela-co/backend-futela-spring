package com.futela.api.domain.port.in.auth;

import java.util.UUID;

public interface SendEmailCodeUseCase {
    void execute(UUID userId);
}
