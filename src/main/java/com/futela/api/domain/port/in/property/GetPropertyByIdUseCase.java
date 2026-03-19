package com.futela.api.domain.port.in.property;

import com.futela.api.application.dto.response.property.PropertyResponse;

public interface GetPropertyByIdUseCase {
    PropertyResponse execute(String slug);
}
