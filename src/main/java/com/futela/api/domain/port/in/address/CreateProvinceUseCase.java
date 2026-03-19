package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.request.address.CreateProvinceRequest;
import com.futela.api.application.dto.response.address.ProvinceResponse;

public interface CreateProvinceUseCase {
    ProvinceResponse execute(CreateProvinceRequest request);
}
