package com.futela.api.domain.port.in.address;

import com.futela.api.application.dto.request.address.CreateTownRequest;
import com.futela.api.application.dto.response.address.TownResponse;

public interface CreateTownUseCase {
    TownResponse execute(CreateTownRequest request);
}
