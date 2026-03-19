package com.futela.api.domain.port.in.property;

import com.futela.api.application.dto.request.property.CreatePropertyRequest;
import com.futela.api.application.dto.response.property.PropertyResponse;

import java.util.UUID;

public interface CreatePropertyUseCase {
    PropertyResponse execute(CreatePropertyRequest request, UUID ownerId, UUID companyId);
}
