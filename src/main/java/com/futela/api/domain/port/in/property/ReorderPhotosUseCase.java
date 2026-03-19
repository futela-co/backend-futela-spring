package com.futela.api.domain.port.in.property;

import com.futela.api.application.dto.response.property.PhotoResponse;

import java.util.List;
import java.util.UUID;

public interface ReorderPhotosUseCase {
    List<PhotoResponse> execute(UUID propertyId, List<UUID> photoIds, UUID ownerId);
}
