package com.futela.api.domain.port.in.property;

import java.util.UUID;

public interface DeletePropertyPhotoUseCase {
    void execute(UUID propertyId, UUID photoId, UUID ownerId);
}
