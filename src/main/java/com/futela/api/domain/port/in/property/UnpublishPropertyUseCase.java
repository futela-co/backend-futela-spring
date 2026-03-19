package com.futela.api.domain.port.in.property;

import com.futela.api.application.dto.response.property.PropertyResponse;

import java.util.UUID;

public interface UnpublishPropertyUseCase {
    PropertyResponse execute(UUID id, UUID ownerId);
}
