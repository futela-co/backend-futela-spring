package com.futela.api.domain.port.in.property;

import com.futela.api.application.dto.response.property.PropertySummaryResponse;
import com.futela.api.domain.model.common.PageResult;

import java.util.UUID;

public interface GetPropertiesByOwnerUseCase {
    PageResult<PropertySummaryResponse> execute(UUID ownerId, int page, int size);
}
