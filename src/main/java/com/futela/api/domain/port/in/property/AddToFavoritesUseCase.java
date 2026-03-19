package com.futela.api.domain.port.in.property;

import java.util.UUID;

public interface AddToFavoritesUseCase {
    void execute(UUID userId, UUID propertyId, UUID companyId);
}
