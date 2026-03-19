package com.futela.api.domain.port.in.property;

import java.util.UUID;

public interface RemoveFromFavoritesUseCase {
    void execute(UUID userId, UUID propertyId);
}
