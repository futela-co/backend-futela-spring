package com.futela.api.domain.port.in.auth;

import com.futela.api.application.dto.response.auth.DeviceSessionResponse;

import java.util.List;
import java.util.UUID;

public interface GetActiveDevicesUseCase {
    List<DeviceSessionResponse> execute(UUID userId, UUID currentSessionId);
}
