package com.futela.api.domain.port.in.auth;

import com.futela.api.application.dto.request.auth.UpdateProfileRequest;
import com.futela.api.application.dto.response.auth.UserResponse;

import java.util.UUID;

public interface UpdateProfileUseCase {
    UserResponse execute(UUID userId, UpdateProfileRequest request);
}
