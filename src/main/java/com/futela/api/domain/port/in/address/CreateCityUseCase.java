package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.request.address.CreateCityRequest;
import com.futela.api.application.dto.response.address.CityResponse;

public interface CreateCityUseCase {
    CityResponse execute(CreateCityRequest request);
}
