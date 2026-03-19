package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.request.address.CreateCountryRequest;
import com.futela.api.application.dto.response.address.CountryResponse;

public interface CreateCountryUseCase {
    CountryResponse execute(CreateCountryRequest request);
}
