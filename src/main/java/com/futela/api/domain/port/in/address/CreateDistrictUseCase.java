package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.request.address.CreateDistrictRequest;
import com.futela.api.application.dto.response.address.DistrictResponse;

public interface CreateDistrictUseCase {
    DistrictResponse execute(CreateDistrictRequest request);
}
