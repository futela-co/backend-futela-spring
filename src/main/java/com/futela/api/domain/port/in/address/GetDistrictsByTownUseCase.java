package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.response.address.DistrictResponse;

import java.util.List;
import java.util.UUID;

public interface GetDistrictsByTownUseCase {
    List<DistrictResponse> execute(UUID townId);
}
