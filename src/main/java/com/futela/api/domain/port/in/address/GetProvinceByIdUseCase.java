package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.response.address.ProvinceResponse;

import java.util.UUID;

public interface GetProvinceByIdUseCase {
    ProvinceResponse execute(UUID id);
}
