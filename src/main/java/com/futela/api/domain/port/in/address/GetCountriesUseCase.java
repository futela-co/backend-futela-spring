package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.response.address.CountryResponse;

import java.util.List;

public interface GetCountriesUseCase {
    List<CountryResponse> execute();
}
