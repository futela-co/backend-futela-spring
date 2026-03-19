package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.response.address.CityResponse;

import java.util.UUID;

public interface GetCityByIdUseCase {
    CityResponse execute(UUID id);
}
