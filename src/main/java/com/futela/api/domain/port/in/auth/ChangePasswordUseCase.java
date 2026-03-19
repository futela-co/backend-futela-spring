package com.futela.api.domain.port.in.auth;

import com.futela.api.application.dto.request.auth.ChangePasswordRequest;

import java.util.UUID;

public interface ChangePasswordUseCase {
    void execute(UUID userId, ChangePasswordRequest request);
}
