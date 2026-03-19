package com.futela.api.domain.port.in.property;

import com.futela.api.application.dto.response.property.PropertyResponse;

import java.util.UUID;

public interface PublishPropertyUseCase {
    PropertyResponse execute(UUID id, UUID ownerId);
}
