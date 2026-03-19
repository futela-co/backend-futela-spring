package com.futela.api.domain.port.in.property;

import com.futela.api.application.dto.response.property.PhotoResponse;

import java.util.UUID;

public interface SetPrimaryPhotoUseCase {
    PhotoResponse execute(UUID propertyId, UUID photoId, UUID ownerId);
}
