package com.futela.api.domain.port.in.property;

import com.futela.api.application.dto.request.property.UpdatePropertyRequest;
import com.futela.api.application.dto.response.property.PropertyResponse;

import java.util.UUID;

public interface UpdatePropertyUseCase {
    PropertyResponse execute(UUID id, UpdatePropertyRequest request, UUID ownerId);
}
