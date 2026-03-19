package com.futela.api.domain.port.in.property;

import com.futela.api.application.dto.response.property.PropertySummaryResponse;

import java.util.List;
import java.util.UUID;

public interface GetUserFavoritesUseCase {
    List<PropertySummaryResponse> execute(UUID userId);
}
