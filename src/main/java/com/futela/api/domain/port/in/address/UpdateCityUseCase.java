package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.request.address.UpdateCityRequest;
import com.futela.api.application.dto.response.address.CityResponse;

import java.util.UUID;

public interface UpdateCityUseCase {
    CityResponse execute(UUID id, UpdateCityRequest request);
}
