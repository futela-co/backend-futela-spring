package com.futela.api.domain.port.in.auth;

import com.futela.api.application.dto.response.auth.UserResponse;

import java.util.UUID;

public interface GetCurrentUserUseCase {
    UserResponse execute(UUID userId);
}
