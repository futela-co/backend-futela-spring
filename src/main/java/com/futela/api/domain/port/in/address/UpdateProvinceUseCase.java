package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.request.address.UpdateProvinceRequest;
import com.futela.api.application.dto.response.address.ProvinceResponse;

import java.util.UUID;

public interface UpdateProvinceUseCase {
    ProvinceResponse execute(UUID id, UpdateProvinceRequest request);
}
