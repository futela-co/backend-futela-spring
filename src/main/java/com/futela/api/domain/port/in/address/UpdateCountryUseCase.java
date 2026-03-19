package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.request.address.UpdateCountryRequest;
import com.futela.api.application.dto.response.address.CountryResponse;

import java.util.UUID;

public interface UpdateCountryUseCase {
    CountryResponse execute(UUID id, UpdateCountryRequest request);
}
