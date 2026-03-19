package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.response.address.ProvinceResponse;

import java.util.List;
import java.util.UUID;

public interface GetProvincesByCountryUseCase {
    List<ProvinceResponse> execute(UUID countryId);
}
